document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    const medicosGrid = document.getElementById('medicosGrid');
    const noResults = document.getElementById('noResults');
    const resultsCount = document.getElementById('resultsCount');
    const specialtyFilters = document.getElementById('specialtyFilters');

    let currentSpecialtyFilter = 'all';

    // Obtener todas las especialidades únicas
    const allCards = document.querySelectorAll('.medico-card');
    const specialties = new Set();

    allCards.forEach(card => {
        const specialty = card.getAttribute('data-especialidad');
        if (specialty) {
            specialties.add(specialty);
        }
    });

    // Crear botones de filtro de especialidad
    specialties.forEach(specialty => {
        const btn = document.createElement('button');
        // Usar clases de Bootstrap para los botones generados dinámicamente
        btn.className = 'btn btn-outline-primary rounded-pill specialty-filter-btn';
        btn.setAttribute('data-specialty', specialty);
        btn.innerHTML = `<i class="bi bi-heart-pulse me-2"></i>${specialty}`;
        btn.addEventListener('click', function () {
            // Remover clase active de todos los botones
            document.querySelectorAll('.specialty-filter-btn').forEach(b => b.classList.remove('active'));
            // Agregar clase active al botón clickeado
            this.classList.add('active');
            currentSpecialtyFilter = specialty;
            filterDoctors();
        });
        specialtyFilters.appendChild(btn);
    });

    // Evento para el botón "Todas"
    const allBtn = document.querySelector('[data-specialty="all"]');
    if (allBtn) {
        allBtn.addEventListener('click', function () {
            document.querySelectorAll('.specialty-filter-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            currentSpecialtyFilter = 'all';
            filterDoctors();
        });
    }

    // Función de filtrado
    function filterDoctors() {
        const searchTerm = searchInput.value.toLowerCase().trim();
        let visibleCount = 0;

        allCards.forEach(card => {
            const nombre = card.getAttribute('data-nombre').toLowerCase();
            const apellido = card.getAttribute('data-apellido').toLowerCase();
            const especialidad = card.getAttribute('data-especialidad').toLowerCase();

            const matchesSearch = nombre.includes(searchTerm) ||
                apellido.includes(searchTerm) ||
                especialidad.includes(searchTerm);

            const matchesSpecialty = currentSpecialtyFilter === 'all' ||
                especialidad === currentSpecialtyFilter.toLowerCase();

            if (matchesSearch && matchesSpecialty) {
                card.style.display = 'block';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });

        // Actualizar contador
        if (resultsCount) resultsCount.textContent = visibleCount;

        // Mostrar/ocultar mensaje de no resultados
        if (visibleCount === 0) {
            noResults.classList.remove('d-none');
            if (medicosGrid) medicosGrid.classList.add('d-none');
        } else {
            noResults.classList.add('d-none');
            if (medicosGrid) medicosGrid.classList.remove('d-none');
        }
    }

    // Evento de búsqueda en tiempo real
    if (searchInput) searchInput.addEventListener('input', filterDoctors);

    // Inicializar contador
    filterDoctors();
});
