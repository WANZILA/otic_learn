package com.otic.learn.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.otic.learn.data.model.CourseDto
import com.otic.learn.data.model.LessonDto
import com.otic.learn.domain.model.Course
import com.otic.learn.domain.model.Lesson
import com.otic.learn.domain.repo.CoursesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CoursesRepositoryImpl(
    private val db: FirebaseFirestore
) : CoursesRepository {

    override fun observeCourses(): Flow<List<Course>> = callbackFlow {
        val sub = db.collection("courses")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.map { doc ->
                    val dto = doc.toObject<CourseDto>() ?: CourseDto()
                    Course(
                        id = doc.id,
                        title = dto.title,
                        description = dto.description,
                        thumbnailUrl = dto.thumbnailUrl
                    )
                }
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    override fun observeLessons(courseId: String): Flow<List<Lesson>> = callbackFlow {
        val sub = db.collection("courses").document(courseId)
            .collection("lessons")
            .orderBy("order")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.map { doc ->
                    val dto = doc.toObject<LessonDto>() ?: LessonDto()
                    Lesson(
                        id = doc.id,
                        courseId = courseId,
                        title = dto.title,
                        content = dto.content,
                        videoUrl = dto.videoUrl,
                        order = dto.order
                    )
                }
                trySend(list)
            }
        awaitClose { sub.remove() }
    }

    override suspend fun getLesson(courseId: String, lessonId: String): Lesson? {
        val doc = db.collection("courses").document(courseId)
            .collection("lessons").document(lessonId).get().await()
        val dto = doc.toObject<LessonDto>() ?: return null
        return Lesson(
            id = doc.id,
            courseId = courseId,
            title = dto.title,
            content = dto.content,
            videoUrl = dto.videoUrl,
            order = dto.order
        )
    }
}
