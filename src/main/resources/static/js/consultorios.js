document.addEventListener('DOMContentLoaded', function () {
    const consultoriosTableBody = document.getElementById('consultoriosTableBody');
    const consultorioModal = new bootstrap.Modal(document.getElementById('consultorioModal'));
    const consultorioForm = document.getElementById('consultorioForm');
    const consultorioModalLabel = document.getElementById('consultorioModalLabel');
    const consultorioIdInput = document.getElementById('consultorioId');
    const numeroInput = document.getElementById('numero');
    const pisoInput = document.getElementById('piso');
    const noConsultoriosMessage = document.getElementById('noConsultoriosMessage');

    // Función para cargar los consultorios
    async function loadConsultorios() {
        try {
            const response = await fetch('/admin/consultorios/api');
            if (!response.ok) {
                throw new Error('Error al cargar consultorios: ' + response.statusText);
            }
            const consultorios = await response.json();
            consultoriosTableBody.innerHTML = ''; // Limpiar tabla

            if (consultorios.length === 0) {
                noConsultoriosMessage.style.display = 'block';
                consultoriosTableBody.style.display = 'none';
            } else {
                noConsultoriosMessage.style.display = 'none';
                consultoriosTableBody.style.display = 'table-row-group'; // Restaurar display por defecto
                consultorios.forEach(consultorio => {
                    const row = consultoriosTableBody.insertRow();
                    const doctorNames = consultorio.doctorNames ? consultorio.doctorNames.join(', ') : 'Ninguno';
                    row.innerHTML = `
                        <td>${consultorio.idConsultorio}</td>
                        <td>${consultorio.numero}</td>
                        <td>${consultorio.piso}</td>
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
        } catch (error) {
            console.error('Error:', error);
            consultoriosTableBody.innerHTML = `<tr><td colspan="6" class="text-danger">Error al cargar los consultorios.</td></tr>`;
            noConsultoriosMessage.style.display = 'none';
            consultoriosTableBody.style.display = 'table-row-group';
        }
    }

    // Cargar consultorios al iniciar la página
    loadConsultorios();

    // Evento para abrir el modal para un nuevo consultorio
    document.getElementById('btnAbrirNuevoConsultorioModal').addEventListener('click', function () {
        consultorioForm.reset(); // Limpiar el formulario
        consultorioIdInput.value = ''; // Asegurarse de que el ID esté vacío
        consultorioModalLabel.textContent = 'Agregar Consultorio';
        consultorioModal.show();
    });

    // Evento para enviar el formulario (agregar/editar)
    consultorioForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const idConsultorio = consultorioIdInput.value;
        const url = idConsultorio ? `/admin/consultorios/api/${idConsultorio}` : '/admin/consultorios/api';
        const method = idConsultorio ? 'PUT' : 'POST';

        const consultorioData = {
            idConsultorio: idConsultorio ? parseInt(idConsultorio) : null,
            numero: numeroInput.value,
            piso: pisoInput.value
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
            loadConsultorios(); // Recargar la lista de consultorios
            // Mostrar mensaje de éxito (opcional)
        } catch (error) {
            console.error('Error al guardar el consultorio:', error);
            alert(error.message); // Mostrar el error al usuario
        }
    });

    // Modal de Ver Detalles
    const verConsultorioModal = new bootstrap.Modal(document.getElementById('verConsultorioModal'));
    const verNumero = document.getElementById('verNumero');
    const verPiso = document.getElementById('verPiso');
    const verListaMedicos = document.getElementById('verListaMedicos');

    // Evento para editar, eliminar o ver (delegación de eventos)
    consultoriosTableBody.addEventListener('click', async function (event) {
        if (event.target.classList.contains('btn-ver') || event.target.closest('.btn-ver')) {
            const button = event.target.classList.contains('btn-ver') ? event.target : event.target.closest('.btn-ver');
            const id = button.dataset.id;

            try {
                // Obtener datos del consultorio
                const response = await fetch(`/admin/consultorios/api/${id}`);
                if (!response.ok) throw new Error('Error al cargar consultorio');
                const consultorio = await response.json();

                verNumero.textContent = consultorio.numero;
                verPiso.textContent = consultorio.piso;

                // Limpiar lista
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
                numeroInput.value = consultorio.numero;
                pisoInput.value = consultorio.piso;
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

                    if (response.status === 409) { // HTTP CONFLICT para médicos asignados
                        const errorMsg = await response.text();
                        alert(errorMsg);
                    } else if (!response.ok) {
                        throw new Error('Error al eliminar consultorio: ' + response.statusText);
                    } else {
                        loadConsultorios(); // Recargar la lista
                        // Mostrar mensaje de éxito (opcional)
                    }
                } catch (error) {
                    console.error('Error al eliminar consultorio:', error);
                    alert(error.message);
                }
            }
        }
    });
});
