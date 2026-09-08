package com.example.ui.home

/**
 * Configuration for toggling visibility of all elements on the Home screen.
 */
data class HomeScreenElementsConfig(
    val showClock: Boolean = true,
    val showDate: Boolean = true,
    val showSolarBadge: Boolean = true,
    val showHeaderActions: Boolean = true,
    val showWidgets: Boolean = true,
    val showMostUsedApps: Boolean = true,
    val showMostUsedHeader: Boolean = true,
    val showSearchBar: Boolean = true,
    val showSwipeUpHint: Boolean = true
) {
    val visibleElementsCount: Int
        get() = listOf(
            showClock,
            showDate,
            showSolarBadge,
            showHeaderActions,
            showWidgets,
            showMostUsedApps,
            showMostUsedHeader,
            showSearchBar,
            showSwipeUpHint
        ).count { it }
}

enum class HomeScreenPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val config: HomeScreenElementsConfig
) {
    FULL(
        id = "full",
        title = "Completo",
        subtitle = "Todos los componentes visibles para máxima funcionalidad",
        config = HomeScreenElementsConfig(
            showClock = true,
            showDate = true,
            showSolarBadge = true,
            showHeaderActions = true,
            showWidgets = true,
            showMostUsedApps = true,
            showMostUsedHeader = true,
            showSearchBar = true,
            showSwipeUpHint = true
        )
    ),
    ULTRA_MINIMAL(
        id = "ultra_minimal",
        title = "Ultra Minimalista",
        subtitle = "Solo reloj y barra de búsqueda para cero distracciones",
        config = HomeScreenElementsConfig(
            showClock = true,
            showDate = true,
            showSolarBadge = false,
            showHeaderActions = true,
            showWidgets = false,
            showMostUsedApps = false,
            showMostUsedHeader = false,
            showSearchBar = true,
            showSwipeUpHint = true
        )
    ),
    ZEN(
        id = "zen",
        title = "Zen Puro",
        subtitle = "Únicamente reloj y fecha en una pantalla despejada",
        config = HomeScreenElementsConfig(
            showClock = true,
            showDate = true,
            showSolarBadge = false,
            showHeaderActions = true,
            showWidgets = false,
            showMostUsedApps = false,
            showMostUsedHeader = false,
            showSearchBar = false,
            showSwipeUpHint = true
        )
    ),
    DASHBOARD(
        id = "dashboard",
        title = "Tablero y Widgets",
        subtitle = "Reloj y widgets de productividad sin lista de apps",
        config = HomeScreenElementsConfig(
            showClock = true,
            showDate = true,
            showSolarBadge = true,
            showHeaderActions = true,
            showWidgets = true,
            showMostUsedApps = false,
            showMostUsedHeader = false,
            showSearchBar = true,
            showSwipeUpHint = true
        )
    );

    companion object {
        fun fromId(id: String): HomeScreenPreset? = entries.find { it.id == id }
    }
}
