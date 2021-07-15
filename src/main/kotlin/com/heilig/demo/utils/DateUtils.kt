package com.heilig.demo.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
class DateUtils {

    companion object{
        val logger: Logger = LoggerFactory.getLogger(DateUtils::class.java.name)

        val FORMATTER = DateTimeFormatter.ISO_DATE

        fun format(localDate: LocalDate): String? {
            Objects.requireNonNull(localDate, "The given date cannot be null!")
            return localDate.format(FORMATTER)
        }

        fun parse(localDateString: String): LocalDate? {
            Objects.requireNonNull(localDateString, "The given String cannot be null!")
            var localDate: LocalDate? = null
            try {
                localDate = LocalDate.parse(localDateString)
            } catch (e: DateTimeParseException) {
                logger.error("Impossible to parse the incoming date '{}' into a LocalDate! Null is returned", localDateString)
            }
            return localDate
        }
    }
}
