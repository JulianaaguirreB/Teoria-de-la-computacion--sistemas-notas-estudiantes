// API URL - cambia el puerto si es necesario
const API_URL = 'http://localhost:8081/api';

// Variables globales
let currentEditId = null;

// Cargar estudiantes al iniciar
document.addEventListener('DOMContentLoaded', () => {
    console.log('Estudiante.js cargado');
    cargarEstudiantes();
    
    const form = document.getElementById('estudianteForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await guardarEstudiante();
        });
    }
});

// Función para mostrar mensajes
function mostrarMensaje(mensaje, tipo) {
    const div = document.getElementById('mensaje');
    if (div) {
        div.textContent = mensaje;
        div.className = `mensaje mensaje-${tipo}`;
        div.style.display = 'block';
        setTimeout(() => {
            div.style.display = 'none';
        }, 3000);
    }
}

// Cargar estudiantes desde el backend
async function cargarEstudiantes() {
    try {
        const response = await fetch(`${API_URL}/estudiantes`);
        if (!response.ok) throw new Error('Error al cargar estudiantes');
        const estudiantes = await response.json();
        mostrarEstudiantes(estudiantes);
    } catch (error) {
        console.error('Error:', error);
        mostrarMensaje('Error al cargar estudiantes', 'error');
        const tbody = document.getElementById('estudiantesList');
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Error al cargar datos</td></tr>';
        }
    }
}

// Mostrar estudiantes en la tabla
function mostrarEstudiantes(estudiantes) {
    const tbody = document.getElementById('estudiantesList');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (estudiantes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">No hay estudiantes registrados</td></tr>';
        return;
    }
    
    estudiantes.forEach(estudiante => {
        const row = tbody.insertRow();
        row.insertCell(0).textContent = estudiante.id;
        row.insertCell(1).textContent = estudiante.nombre;
        row.insertCell(2).textContent = estudiante.apellido;
        row.insertCell(3).textContent = estudiante.correo;
        
        const accionesCell = row.insertCell(4);
        accionesCell.className = 'action-buttons';
        
        const editBtn = document.createElement('button');
        editBtn.textContent = 'Editar';
        editBtn.className = 'btn-edit';
        editBtn.onclick = () => editarEstudiante(estudiante.id);
        
        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Eliminar';
        deleteBtn.className = 'btn-delete';
        deleteBtn.onclick = () => eliminarEstudiante(estudiante.id);
        
        const notasBtn = document.createElement('button');
        notasBtn.textContent = 'Ver Notas';
        notasBtn.className = 'btn-notas';
        notasBtn.onclick = () => window.location.href = `nota.html?id=${estudiante.id}`;
        
        const calcularBtn = document.createElement('button');
        calcularBtn.textContent = 'Calcular NF';
        calcularBtn.className = 'btn-calcular';
        calcularBtn.onclick = () => calcularNotaFinal(estudiante.id);
        
        accionesCell.appendChild(editBtn);
        accionesCell.appendChild(deleteBtn);
        accionesCell.appendChild(notasBtn);
        accionesCell.appendChild(calcularBtn);
    });
}

// Abrir formulario para crear estudiante
window.abrirFormulario = function() {
    currentEditId = null;
    const modal = document.getElementById('estudianteModal');
    const titulo = document.getElementById('modalTitulo');
    const form = document.getElementById('estudianteForm');
    const estudianteId = document.getElementById('estudianteId');
    
    if (titulo) titulo.textContent = 'Crear Estudiante';
    if (estudianteId) estudianteId.value = '';
    if (form) form.reset();
    if (modal) modal.style.display = 'block';
};

// Cerrar modal
window.cerrarModal = function() {
    const modal = document.getElementById('estudianteModal');
    if (modal) modal.style.display = 'none';
};

// Editar estudiante
window.editarEstudiante = async function(id) {
    try {
        const response = await fetch(`${API_URL}/estudiantes/${id}`);
        const estudiante = await response.json();
        
        const modal = document.getElementById('estudianteModal');
        const titulo = document.getElementById('modalTitulo');
        const estudianteId = document.getElementById('estudianteId');
        const nombre = document.getElementById('nombre');
        const apellido = document.getElementById('apellido');
        const correo = document.getElementById('correo');
        
        if (titulo) titulo.textContent = 'Editar Estudiante';
        if (estudianteId) estudianteId.value = estudiante.id;
        if (nombre) nombre.value = estudiante.nombre;
        if (apellido) apellido.value = estudiante.apellido;
        if (correo) correo.value = estudiante.correo;
        if (modal) modal.style.display = 'block';
    } catch (error) {
        mostrarMensaje('Error al cargar el estudiante', 'error');
    }
};

// Guardar estudiante (crear o actualizar)
async function guardarEstudiante() {
    const id = document.getElementById('estudianteId').value;
    const estudiante = {
        nombre: document.getElementById('nombre').value,
        apellido: document.getElementById('apellido').value,
        correo: document.getElementById('correo').value
    };
    
    try {
        let response;
        if (id) {
            response = await fetch(`${API_URL}/estudiantes/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(estudiante)
            });
        } else {
            response = await fetch(`${API_URL}/estudiantes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(estudiante)
            });
        }
        
        if (response.ok) {
            mostrarMensaje(id ? 'Estudiante actualizado' : 'Estudiante creado', 'exito');
            cerrarModal();
            cargarEstudiantes();
        } else {
            const error = await response.text();
            mostrarMensaje('Error: ' + error, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarMensaje('Error al guardar el estudiante', 'error');
    }
}

// Eliminar estudiante
window.eliminarEstudiante = async function(id) {
    if (confirm('¿Estás seguro de eliminar este estudiante? Se eliminarán todas sus notas.')) {
        try {
            const response = await fetch(`${API_URL}/estudiantes/${id}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                mostrarMensaje('Estudiante eliminado', 'exito');
                cargarEstudiantes();
            } else {
                mostrarMensaje('Error al eliminar el estudiante', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            mostrarMensaje('Error al eliminar el estudiante', 'error');
        }
    }
};

// Calcular nota final
window.calcularNotaFinal = async function(id) {
    try {
        const response = await fetch(`${API_URL}/estudiantes/${id}/nota-final`);
        if (response.ok) {
            const data = await response.json();
            const notaFinal = data.notaFinal;
            const color = notaFinal >= 3 ? 'green' : 'red';
            const estado = notaFinal >= 3 ? 'APROBADO' : 'REPROBADO';
            
            const resultadoDiv = document.getElementById('notaFinalResultado');
            const modal = document.getElementById('notaFinalModal');
            
            if (resultadoDiv) {
                resultadoDiv.innerHTML = `
                    <div style="text-align: center;">
                        <h3 style="color: ${color};">Nota Final: ${notaFinal}</h3>
                        <p><strong>${estado}</strong></p>
                    </div>
                `;
            }
            if (modal) modal.style.display = 'block';
        } else {
            const error = await response.json();
            mostrarMensaje('Error: ' + (error.message || 'No se pudo calcular'), 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarMensaje('Error al calcular la nota final', 'error');
    }
};

// Cerrar modal nota final
window.cerrarModalNotaFinal = function() {
    const modal = document.getElementById('notaFinalModal');
    if (modal) modal.style.display = 'none';
};

// Ver notas (navegar a la página de notas)
window.verNotas = function(id) {
    window.location.href = `nota.html?id=${id}`;
};