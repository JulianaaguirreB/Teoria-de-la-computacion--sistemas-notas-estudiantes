// API URL - cambia el puerto si es necesario
const API_URL = 'http://localhost:8081/api';

// Variables globales
let estudianteId = null;
let notaIdActual = null;

// Obtener el ID del estudiante de la URL
document.addEventListener('DOMContentLoaded', () => {
    console.log('Nota.js cargado');
    const urlParams = new URLSearchParams(window.location.search);
    estudianteId = urlParams.get('id');
    
    if (estudianteId) {
        cargarInformacionEstudiante();
        cargarNotas();
    } else {
        alert('No se especificó un estudiante válido');
        window.location.href = 'index.html';
    }
});

// Cargar información del estudiante
async function cargarInformacionEstudiante() {
    try {
        const response = await fetch(`${API_URL}/estudiantes/${estudianteId}`);
        if (response.ok) {
            const estudiante = await response.json();
            const infoDiv = document.getElementById('estudianteInfo');
            if (infoDiv) {
                infoDiv.innerHTML = `
                    <h2>Gestión de Notas para: ${estudiante.nombre} ${estudiante.apellido}</h2>
                    <p><strong>Correo:</strong> ${estudiante.correo}</p>
                    <button class="button" onclick="volver()" style="background: #666;">Volver</button>
                `;
            }
        } else {
            alert('Error al cargar la información del estudiante');
        }
    } catch (error) {
        console.error('Error cargando estudiante:', error);
        alert('Error al cargar la información del estudiante');
    }
}

// Cargar notas del estudiante
async function cargarNotas() {
    try {
        const response = await fetch(`${API_URL}/notas/estudiante/${estudianteId}`);
        const notas = await response.json();
        mostrarNotas(notas);
    } catch (error) {
        console.error('Error cargando notas:', error);
        alert('Error al cargar las notas');
    }
}

// Mostrar notas en la tabla
function mostrarNotas(notas) {
    const tbody = document.getElementById('notasList');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (notas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">No hay notas registradas para este estudiante</td></tr>';
        return;
    }
    
    let sumaPorcentajes = 0;
    let sumaContribuciones = 0;
    
    notas.forEach(nota => {
        const contribucion = (nota.valor * nota.porcentaje) / 100;
        sumaPorcentajes += nota.porcentaje;
        sumaContribuciones += contribucion;
        
        const row = tbody.insertRow();
        row.insertCell(0).textContent = nota.id;
        row.insertCell(1).textContent = nota.materia;
        row.insertCell(2).textContent = nota.observacion || '-';
        row.insertCell(3).textContent = nota.valor;
        row.insertCell(4).textContent = nota.porcentaje + '%';
        row.insertCell(5).textContent = contribucion.toFixed(2);
        
        const actionsCell = row.insertCell(6);
        actionsCell.className = 'action-buttons';
        
        const editBtn = document.createElement('button');
        editBtn.textContent = 'Editar';
        editBtn.className = 'btn-edit';
        editBtn.onclick = () => editarNota(nota);
        
        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Eliminar';
        deleteBtn.className = 'btn-delete';
        deleteBtn.onclick = () => eliminarNota(nota.id);
        
        actionsCell.appendChild(editBtn);
        actionsCell.appendChild(deleteBtn);
    });
    
    // Actualizar resumen
    actualizarResumen(notas, sumaPorcentajes, sumaContribuciones);
}

// Actualizar el resumen de calificaciones
function actualizarResumen(notas, sumaPorcentajes, sumaContribuciones) {
    const notaActual = sumaContribuciones;
    const resumenDiv = document.getElementById('resumen');
    const porcentajeRestante = 100 - sumaPorcentajes;
    
    let notaNecesaria = null;
    if (porcentajeRestante > 0 && porcentajeRestante < 100) {
        const notaDeseada = 3.0;
        notaNecesaria = ((notaDeseada - notaActual) * 100) / porcentajeRestante;
        notaNecesaria = Math.max(0, Math.min(5, notaNecesaria));
    }
    
    if (resumenDiv) {
        resumenDiv.innerHTML = `
            <h3>Resumen de Calificaciones</h3>
            <p><strong>Suma de Porcentajes:</strong> ${sumaPorcentajes.toFixed(1)}%</p>
            <p><strong>Nota Acumulada Actual:</strong> ${notaActual.toFixed(2)}</p>
            <p><strong>Porcentaje Restante:</strong> ${porcentajeRestante.toFixed(1)}%</p>
            ${notaNecesaria !== null ? `<p><strong>Nota necesaria para aprobar (3.0):</strong> ${notaNecesaria.toFixed(2)}</p>` : ''}
            ${sumaPorcentajes === 100 ? `<p><strong>Nota Final:</strong> ${notaActual.toFixed(2)} - ${notaActual >= 3 ? 'APROBADO' : 'REPROBADO'}</p>` : ''}
            ${sumaPorcentajes > 100 ? '<p style="color: red;"><strong>Error:</strong> La suma de porcentajes supera el 100%</p>' : ''}
        `;
    }
}

// Abrir formulario para agregar nota
window.abrirFormularioNota = function() {
    notaIdActual = null;
    const modal = document.getElementById('notaModal');
    const titulo = document.getElementById('modalTitulo');
    const form = document.getElementById('notaForm');
    
    if (titulo) titulo.textContent = 'Agregar Nota';
    if (form) form.reset();
    if (modal) {
        document.getElementById('notaId').value = '';
        modal.style.display = 'block';
    }
};

// Cerrar modal de nota
window.cerrarModalNota = function() {
    const modal = document.getElementById('notaModal');
    if (modal) modal.style.display = 'none';
};

// Editar nota
window.editarNota = function(nota) {
    notaIdActual = nota.id;
    const modal = document.getElementById('notaModal');
    const titulo = document.getElementById('modalTitulo');
    
    if (titulo) titulo.textContent = 'Editar Nota';
    document.getElementById('notaId').value = nota.id;
    document.getElementById('materia').value = nota.materia;
    document.getElementById('observacion').value = nota.observacion || '';
    document.getElementById('valor').value = nota.valor;
    document.getElementById('porcentaje').value = nota.porcentaje;
    if (modal) modal.style.display = 'block';
};

// Guardar nota (crear o actualizar)
window.guardarNota = async function() {
    const id = document.getElementById('notaId').value;
    const nota = {
        materia: document.getElementById('materia').value,
        observacion: document.getElementById('observacion').value,
        valor: parseFloat(document.getElementById('valor').value),
        porcentaje: parseFloat(document.getElementById('porcentaje').value),
        estudianteId: parseInt(estudianteId)
    };
    
    // Validaciones
    if (nota.valor < 0 || nota.valor > 5) {
        alert('La nota debe estar entre 0 y 5');
        return;
    }
    
    if (nota.porcentaje < 0 || nota.porcentaje > 100) {
        alert('El porcentaje debe estar entre 0 y 100');
        return;
    }
    
    try {
        let response;
        if (id) {
            response = await fetch(`${API_URL}/notas/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(nota)
            });
        } else {
            response = await fetch(`${API_URL}/notas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(nota)
            });
        }
        
        if (response.ok) {
            alert(id ? 'Nota actualizada' : 'Nota agregada');
            cerrarModalNota();
            cargarNotas();
        } else {
            const error = await response.json();
            alert('Error: ' + (error.message || 'Ocurrió un error'));
        }
    } catch (error) {
        console.error('Error guardando nota:', error);
        alert('Error al guardar la nota');
    }
};

// Eliminar nota
window.eliminarNota = async function(id) {
    if (confirm('¿Estás seguro de eliminar esta nota?')) {
        try {
            const response = await fetch(`${API_URL}/notas/${id}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                alert('Nota eliminada');
                cargarNotas();
            } else {
                alert('Error al eliminar la nota');
            }
        } catch (error) {
            console.error('Error eliminando nota:', error);
            alert('Error al eliminar la nota');
        }
    }
};

window.calcularNotaFinal = async function() {
    console.log('Calculando nota final...');
    console.log('Estudiante ID:', estudianteId);
    
    if (!estudianteId) {
        alert('No hay estudiante seleccionado');
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/estudiantes/${estudianteId}/nota-final`);
        console.log('Response status:', response.status);
        
        if (response.ok) {
            const data = await response.json();
            console.log('Data recibida:', data);
            const notaFinal = data.notaFinal;
            
            // Obtener el contenedor del resultado
            const resultadoDiv = document.getElementById('notaFinalResultado');
            
            if (resultadoDiv) {
                // Agregar contenido al div
                resultadoDiv.innerHTML = `
                    <div style="text-align: center; padding: 20px;">
                        <h3 style="color: ${notaFinal >= 3 ? '#4CAF50' : '#f44336'}; font-size: 24px;">
                            ${notaFinal >= 3 ? '✅' : '❌'} Nota Final: ${notaFinal}
                        </h3>
                        <p style="font-size: 18px; margin-top: 10px;">
                            <strong>${notaFinal >= 3 ? 'APROBADO' : 'REPROBADO'}</strong>
                        </p>
                        <button onclick="cerrarModalNotaFinal()" style="margin-top: 20px; padding: 10px 20px; background: #9C27B0; color: white; border: none; border-radius: 5px; cursor: pointer;">
                            Cerrar
                        </button>
                    </div>
                `;
            } else {
                console.error('No se encontró el div notaFinalResultado');
                alert('Error: No se encontró el contenedor del resultado');
            }
            
            // Mostrar el modal
            const modal = document.getElementById('notaFinalModal');
            if (modal) {
                modal.style.display = 'block';
                console.log('Modal mostrado');
            } else {
                console.error('No se encontró el modal notaFinalModal');
                alert('Nota Final: ' + notaFinal);
            }
        } else {
            const error = await response.json();
            console.error('Error response:', error);
            alert('Error: ' + (error.message || 'No se pudo calcular la nota final'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al calcular la nota final: ' + error.message);
    }
};

// Función para cerrar modal
window.cerrarModalNotaFinal = function() {
    const modal = document.getElementById('notaFinalModal');
    if (modal) {
        modal.style.display = 'none';
    }
};

// Volver a la página principal
window.volver = function() {
    window.location.href = 'index.html';
};