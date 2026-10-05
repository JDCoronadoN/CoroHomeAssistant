package com.example.login_app.domain.usecase
import com.example.login_app.domain.repository.SessionRepository

class CheckSessionUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Boolean = sessionRepository.isSessionActive()
}