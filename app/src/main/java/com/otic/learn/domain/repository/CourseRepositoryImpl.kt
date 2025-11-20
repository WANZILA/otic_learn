package com.otic.learn.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.otic.learn.domain.model.Course
import com.otic.learn.domain.model.Enrollment
import com.otic.learn.domain.model.EnrollmentStatus
import com.otic.learn.domain.repository.CourseRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseRepository {

    override fun observeEnrolledCourses(
        studentId: String
    ): Flow<List<Pair<Course, Enrollment>>> = callbackFlow {

        val query = firestore.collection("enrollments")
            .whereEqualTo("studentId", studentId)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val docs = snapshot?.documents ?: emptyList()

            // Load courses for each enrollment in a coroutine
            launch {
                val enrollments = docs.mapNotNull { it.toEnrollment() }
                val result = mutableListOf<Pair<Course, Enrollment>>()

                for (enrollment in enrollments) {
                    val courseDoc = firestore
                        .collection("courses")
                        .document(enrollment.courseId)
                        .get()
                        .await()

                    val course = courseDoc.toCourse()
                    if (course != null) {
                        result.add(course to enrollment)
                    }
                }

                trySend(result).isSuccess
            }
        }

        awaitClose {
            registration.remove()
        }
    }
}

// ---- Mapping helpers ----

private fun DocumentSnapshot.toEnrollment(): Enrollment? {
    val courseId = getString("courseId") ?: return null
    val studentId = getString("studentId") ?: return null

    val statusString = getString("status") ?: "ACTIVE"
    val status = runCatching { EnrollmentStatus.valueOf(statusString) }
        .getOrElse { EnrollmentStatus.ACTIVE }

    val startDateMillis = getLong("startDateMillis") ?: 0L
    val endDateMillis = getLong("endDateMillis")
    val percentComplete = (getLong("percentComplete") ?: 0L).toInt()
    val lastAccessedAtMillis = getLong("lastAccessedAtMillis")

    return Enrollment(
        id = id,
        courseId = courseId,
        studentId = studentId,
        status = status,
        startDateMillis = startDateMillis,
        endDateMillis = endDateMillis,
        percentComplete = percentComplete,
        lastAccessedAtMillis = lastAccessedAtMillis
    )
}

private fun DocumentSnapshot.toCourse(): Course? {
    val title = getString("title") ?: return null
    val instructorId = getString("instructorId") ?: return null

    val category = getString("category")
    val coverImageUrl = getString("coverImageUrl")
    val level = getString("level")
    val shortDescription = getString("shortDescription")
    val createdAtMillis = getLong("createdAtMillis") ?: 0L

    return Course(
        id = id,
        title = title,
        category = category,
        coverImageUrl = coverImageUrl,
        level = level,
        shortDescription = shortDescription,
        instructorId = instructorId,
        createdAtMillis = createdAtMillis
    )
}
