package com.heilig.demo.repository

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
class RepositoryException : RuntimeException {

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}