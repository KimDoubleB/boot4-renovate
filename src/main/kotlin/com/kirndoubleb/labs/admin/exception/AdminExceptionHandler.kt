package com.kirndoubleb.labs.admin.exception

import com.kirndoubleb.labs.exception.DuplicateResourceException
import com.kirndoubleb.labs.exception.ResourceNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@ControllerAdvice(basePackages = ["com.kirndoubleb.labs.admin.controller"])
class AdminExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(
        ex: ResourceNotFoundException,
        redirectAttributes: RedirectAttributes
    ): String {
        redirectAttributes.addFlashAttribute("errorMessage", "요청하신 데이터를 찾을 수 없습니다: ${ex.message}")
        return "redirect:/admin"
    }

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicateResource(
        ex: DuplicateResourceException,
        redirectAttributes: RedirectAttributes
    ): String {
        redirectAttributes.addFlashAttribute("errorMessage", "중복된 데이터가 존재합니다: ${ex.message}")
        return "redirect:/admin"
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        redirectAttributes: RedirectAttributes
    ): String {
        redirectAttributes.addFlashAttribute("errorMessage", "오류가 발생했습니다: ${ex.message}")
        return "redirect:/admin"
    }
}
