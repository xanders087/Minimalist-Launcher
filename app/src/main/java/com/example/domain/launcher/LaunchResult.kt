package com.example.domain.launcher

/**
 * Resultado tipado y exhaustivo del intento de lanzamiento de una aplicación,
 * permitiendo una gestión granular de estados y errores en la UI del Launcher.
 */
sealed interface LaunchResult {

    /**
     * La aplicación fue despachada y lanzada con éxito en el sistema.
     */
    data object Success : LaunchResult

    /**
     * La aplicación o actividad objetivo no fue encontrada en el dispositivo
     * (posiblemente desinstalada recientemente, deshabilitada o con paquete inexistente).
     */
    data class AppNotFound(
        val packageName: String,
        val message: String? = null
    ) : LaunchResult

    /**
     * Error de permisos o seguridad al invocar la actividad
     * (ej. android:exported="false", restricciones de dispositivo o permisos protegidos).
     */
    data class PermissionDenied(
        val packageName: String,
        val securityException: SecurityException
    ) : LaunchResult

    /**
     * Error genérico inesperado durante la resolución o despacho del Intent.
     */
    data class GenericError(
        val throwable: Throwable
    ) : LaunchResult
}
