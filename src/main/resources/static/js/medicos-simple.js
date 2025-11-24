// Búsqueda de médicos por nombre y especialidad con filtro dropdown
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    const specialtyFilter = document.getElementById('specialtyFilter');
    const medicosGrid = document.getElementById('medicosGrid');
    const noResults = document.getElementById('noResults');
    const resultsCount = document.getElementById('resultsCount');

    if (!searchInput || !medicosGrid || !specialtyFilter) return;

    const medicoCards = medicosGrid.querySelectorAll('.medico-card');

    // Poblar el dropdown de especialidades
    function populateSpecialtyFilter() {
        const specialties = new Set();
        medicoCards.forEach(card => {
            const especialidad = card.getAttribute('data-especialidad');
            if (especialidad) {
                specialties.add(especialidad);
            }
        });

        // Ordenar alfabéticamente
        const sortedSpecialties = Array.from(specialties).sort();

        // Agregar opciones al select
        sortedSpecialties.forEach(specialty => {
            const option = document.createElement('option');
            option.value = specialty;
            option.textContent = specialty;
            specialtyFilter.appendChild(option);
        });
    }

    function updateResults() {
        const searchTerm = searchInput.value.toLowerCase().trim();
        const selectedSpecialty = specialtyFilter.value;
        let visibleCount = 0;

        medicoCards.forEach(card => {
            const nombre = card.getAttribute('data-nombre').toLowerCase();
            const apellido = card.getAttribute('data-apellido').toLowerCase();
            const especialidad = card.getAttribute('data-especialidad');
            const nombreCompleto = `${nombre} ${apellido}`;

            // Filtrar por nombre
            const matchesSearch = searchTerm === '' || nombreCompleto.includes(searchTerm);

            // Filtrar por especialidad
            const matchesSpecialty = selectedSpecialty === '' || especialidad === selectedSpecialty;

            if (matchesSearch && matchesSpecialty) {
                card.style.display = '';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });

        // Update results count
        if (resultsCount) {
            resultsCount.textContent = visibleCount;
        }

        // Show/hide no results message
        if (noResults) {
            if (visibleCount === 0) {
                noResults.classList.remove('d-none');
                medicosGrid.classList.add('d-none');
            } else {
                noResults.classList.add('d-none');
                medicosGrid.classList.remove('d-none');
            }
        }
    }

    // Initialize
    populateSpecialtyFilter();

    // Initialize count
    if (resultsCount) {
        resultsCount.textContent = medicoCards.length;
    }

    // Event listeners
    searchInput.addEventListener('input', updateResults);
    specialtyFilter.addEventListener('change', updateResults);
});
