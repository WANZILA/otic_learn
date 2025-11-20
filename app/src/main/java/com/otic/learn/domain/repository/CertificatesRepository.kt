package com.otic.learn.domain.repository

import com.otic.learn.domain.model.Certificate
import kotlinx.coroutines.flow.Flow

interface CertificatesRepository {

    fun observeCertificatesForStudent(
        studentId: String
    ): Flow<List<Certificate>>
}
