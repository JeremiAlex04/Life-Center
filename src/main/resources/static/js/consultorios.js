document.addEventListener('DOMContentLoaded', function () {
    const consultoriosTableBody = document.getElementById('consultoriosTableBody');
    const consultorioModal = new bootstrap.Modal(document.getElementById('consultorioModal'));
    const consultorioForm = document.getElementById('consultorioForm');
    const consultorioModalLabel = document.getElementById('consultorioModalLabel');
    const consultorioIdInput = document.getElementById('consultorioId');
    const numeroSelect = document.getElementById('numero');
    const pisoSelect = document.getElementById('piso');
    const tipoSelect = document.getElementById('tipo');
    const estadoSelect = document.getElementById('estado');
    const noConsultoriosMessage = document.getElementById('noConsultoriosMessage');

    // Variables para búsqueda y filtrado
    let allConsultorios = [];
    const busquedaNumeroInput = document.getElementById('busquedaNumero');
    const filtroPisoSelect = document.getElementById('filtroPiso');
    const btnBuscar = document.getElementById('btnBuscar');

    // Función para cargar los consultorios
    async function loadConsultorios() {
        try {
            const response = await fetch('/admin/consultorios/api');
            if (!response.ok) {
                throw new Error('Error al cargar consultorios: ' + response.statusText);
            }
            allConsultorios = await response.json();

            populatePisoFilter();
            renderTable(allConsultorios);

        } catch (error) {
            console.error('Error:', error);
            consultoriosTableBody.innerHTML = `<tr><td colspan="8" class="text-danger">Error al cargar los consultorios.</td></tr>`;
            noConsultoriosMessage.style.display = 'none';
            consultoriosTableBody.style.display = 'table-row-group';
        }
    }

    // Función para poblar el filtro de pisos
    function populatePisoFilter() {
        const pisos = [...new Set(allConsultorios.map(c => c.piso))].sort();
        // Guardar selección actual si existe
        const currentSelection = filtroPisoSelect.value;

        filtroPisoSelect.innerHTML = '<option value="">Todos los pisos</option>';
        pisos.forEach(piso => {
            const option = document.createElement('option');
            option.value = piso;
            option.textContent = piso;
            filtroPisoSelect.appendChild(option);
        });

        // Restaurar selección si es posible
        if (currentSelection && pisos.includes(currentSelection)) {
            filtroPisoSelect.value = currentSelection;
        }
    }

    // Función para renderizar la tabla
    function renderTable(consultorios) {
        consultoriosTableBody.innerHTML = ''; // Limpiar tabla

        if (consultorios.length === 0) {
            noConsultoriosMessage.style.display = 'block';
            consultoriosTableBody.style.display = 'none';
            if (allConsultorios.length > 0) {
                noConsultoriosMessage.textContent = 'No se encontraron consultorios con los criterios de búsqueda.';
            } else {
                noConsultoriosMessage.textContent = 'No hay consultorios registrados.';
            }
        } else {
            noConsultoriosMessage.style.display = 'none';
            consultoriosTableBody.style.display = 'table-row-group';

            consultorios.forEach(consultorio => {
                const row = consultoriosTableBody.insertRow();
                const doctorNames = consultorio.doctorNames ? consultorio.doctorNames.join(', ') : 'Ninguno';

                // Badge para estado
                let estadoBadge = '';
                switch (consultorio.estado) {
                    case 'DISPONIBLE': estadoBadge = '<span class="badge bg-success">Disponible</span>'; break;
                    case 'MANTENIMIENTO': estadoBadge = '<span class="badge bg-warning text-dark">Mantenimiento</span>'; break;
                    case 'LIMPIEZA': estadoBadge = '<span class="badge bg-info text-dark">Limpieza</span>'; break;
                    case 'FUERA_SERVICIO': estadoBadge = '<span class="badge bg-danger">Fuera de Servicio</span>'; break;
                    default: estadoBadge = `<span class="badge bg-secondary">${consultorio.estado || 'N/A'}</span>`;
                }

                row.innerHTML = `
                    <td>${consultorio.idConsultorio}</td>
                    <td>${consultorio.numero}</td>
                    <td>${consultorio.piso}</td>
                    <td>${consultorio.tipo || 'General'}</td>
                    <td>${estadoBadge}</td>
                    <td>${consultorio.doctorCount}</td>
                    <td>${doctorNames}</td>
                    <td>
                        <button class="btn btn-sm btn-secondary btn-ver" data-id="${consultorio.idConsultorio}">
                            <i class="bi bi-eye"></i> Ver
                        </button>
                        <button class="btn btn-sm btn-info btn-editar" data-id="${consultorio.idConsultorio}">
                            <i class="bi bi-pencil"></i> Editar
                        </button>
                        <button class="btn btn-sm btn-danger btn-eliminar" data-id="${consultorio.idConsultorio}">
                            <i class="bi bi-trash"></i> Eliminar
                        </button>
                    </td>
                `;
            });
        }
    }

    // Lógica para poblar números de consultorio según el piso
    function populateNumeroOptions(piso, selectedNumero = null) {
        numeroSelect.innerHTML = '<option value="" selected disabled>Seleccione un número</option>';

        if (!piso) {
            numeroSelect.disabled = true;
            return;
        }

        numeroSelect.disabled = false;
        const start = parseInt(piso) * 100 + 1; // Ej: Piso 1 -> 101
        const end = start + 4; // Ej: 101 al 105 (5 consultorios por piso)

        let foundSelected = false;

        for (let i = start; i <= end; i++) {
            const option = document.createElement('option');
            option.value = i;
            option.textContent = i;
            if (selectedNumero && i == selectedNumero) {
                option.selected = true;
                foundSelected = true;
            }
            numeroSelect.appendChild(option);
        }

        // Si el número seleccionado no está en el rango estándar (ej. datos antiguos), agregarlo
        if (selectedNumero && !foundSelected) {
            const option = document.createElement('option');
            option.value = selectedNumero;
            option.textContent = selectedNumero + ' (Actual)';
            option.selected = true;
            numeroSelect.appendChild(option);
        }
    }

    // Evento cambio de piso
    pisoSelect.addEventListener('change', function () {
        populateNumeroOptions(this.value);
    });

    // Evento de búsqueda
    btnBuscar.addEventListener('click', function () {
        const numeroBusqueda = busquedaNumeroInput.value.toLowerCase().trim();
        const pisoFiltro = filtroPisoSelect.value;

        const filtered = allConsultorios.filter(c => {
            const matchNumero = c.numero.toLowerCase().includes(numeroBusqueda);
            const matchPiso = pisoFiltro === '' || c.piso == pisoFiltro;
            return matchNumero && matchPiso;
        });

        renderTable(filtered);
    });

    busquedaNumeroInput.addEventListener('keyup', function (event) {
        if (event.key === 'Enter') {
            btnBuscar.click();
        }
    });

    loadConsultorios();

    document.getElementById('btnAbrirNuevoConsultorioModal').addEventListener('click', function () {
        consultorioForm.reset();
        consultorioIdInput.value = '';
        consultorioModalLabel.textContent = 'Agregar Consultorio';
        numeroSelect.disabled = true; // Deshabilitar número al inicio
        numeroSelect.innerHTML = '<option value="" selected disabled>Seleccione un piso primero</option>';
        // Valores por defecto
        tipoSelect.value = 'GENERAL';
        estadoSelect.value = 'DISPONIBLE';
        consultorioModal.show();
    });

    consultorioForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const idConsultorio = consultorioIdInput.value;
        const url = idConsultorio ? `/admin/consultorios/api/${idConsultorio}` : '/admin/consultorios/api';
        const method = idConsultorio ? 'PUT' : 'POST';

        const consultorioData = {
            idConsultorio: idConsultorio ? parseInt(idConsultorio) : null,
            numero: numeroSelect.value,
            piso: pisoSelect.value,
            tipo: tipoSelect.value,
            estado: estadoSelect.value
        };

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    [document.querySelector('meta[name="_csrf_header"]').content]: document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(consultorioData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Error al guardar el consultorio');
            }

            consultorioModal.hide();
            loadConsultorios();
        } catch (error) {
            console.error('Error al guardar el consultorio:', error);
            alert(error.message);
        }
    });

    // Modal de Ver Detalles
    const verConsultorioModal = new bootstrap.Modal(document.getElementById('verConsultorioModal'));
    const verNumero = document.getElementById('verNumero');
    const verPiso = document.getElementById('verPiso');
    const verTipo = document.getElementById('verTipo');
    const verEstado = document.getElementById('verEstado');
    const verListaMedicos = document.getElementById('verListaMedicos');

    consultoriosTableBody.addEventListener('click', async function (event) {
        if (event.target.classList.contains('btn-ver') || event.target.closest('.btn-ver')) {
            const button = event.target.classList.contains('btn-ver') ? event.target : event.target.closest('.btn-ver');
            const id = button.dataset.id;

            try {
                const response = await fetch(`/admin/consultorios/api/${id}`);
                if (!response.ok) throw new Error('Error al cargar consultorio');
                const consultorio = await response.json();

                verNumero.textContent = consultorio.numero;
                verPiso.textContent = consultorio.piso;
                verTipo.textContent = consultorio.tipo || 'General';
                verEstado.textContent = consultorio.estado || 'Disponible';

                verListaMedicos.innerHTML = '';
                if (consultorio.doctorNames && consultorio.doctorNames.length > 0) {
                    consultorio.doctorNames.forEach(name => {
                        const li = document.createElement('li');
                        li.className = 'list-group-item';
                        li.textContent = name;
                        verListaMedicos.appendChild(li);
                    });
                } else {
                    const li = document.createElement('li');
                    li.className = 'list-group-item text-muted';
                    li.textContent = 'No hay médicos asignados.';
                    verListaMedicos.appendChild(li);
                }

                verConsultorioModal.show();
            } catch (error) {
                console.error('Error:', error);
                alert('No se pudo cargar los detalles.');
            }
        } else if (event.target.classList.contains('btn-editar') || event.target.closest('.btn-editar')) {
            const button = event.target.classList.contains('btn-editar') ? event.target : event.target.closest('.btn-editar');
            const id = button.dataset.id;
            consultorioModalLabel.textContent = 'Editar Consultorio';

            try {
                const response = await fetch(`/admin/consultorios/api/${id}`);
                if (!response.ok) {
                    throw new Error('Error al cargar consultorio para edición: ' + response.statusText);
                }
                const consultorio = await response.json();
                consultorioIdInput.value = consultorio.idConsultorio;

                // Establecer valores
                pisoSelect.value = consultorio.piso;
                tipoSelect.value = consultorio.tipo || 'GENERAL';
                estadoSelect.value = consultorio.estado || 'DISPONIBLE';

                // Poblar opciones de número basadas en el piso y seleccionar el correcto
                populateNumeroOptions(consultorio.piso, consultorio.numero);

                consultorioModal.show();
            } catch (error) {
                console.error('Error al obtener consultorio:', error);
                alert('No se pudo cargar el consultorio para edición.');
            }
        } else if (event.target.classList.contains('btn-eliminar') || event.target.closest('.btn-eliminar')) {
            const button = event.target.classList.contains('btn-eliminar') ? event.target : event.target.closest('.btn-eliminar');
            const id = button.dataset.id;

            if (confirm('¿Está seguro de que desea eliminar este consultorio?')) {
                try {
                    const response = await fetch(`/admin/consultorios/api/${id}`, {
                        method: 'DELETE',
                        headers: {
                            [document.querySelector('meta[name="_csrf_header"]').content]: document.querySelector('meta[name="_csrf"]').content
                        }
                    });

                    if (response.status === 409) {
                        const errorMsg = await response.text();
                        alert(errorMsg);
                    } else if (!response.ok) {
                        throw new Error('Error al eliminar consultorio: ' + response.statusText);
                    } else {
                        loadConsultorios();
                    }
                } catch (error) {
                    console.error('Error al eliminar consultorio:', error);
                    alert(error.message);
                }
            }
        }
    });
});
