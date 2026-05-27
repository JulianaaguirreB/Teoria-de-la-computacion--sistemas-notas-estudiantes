const API_URL = 'http://localhost:8081/api';
let estudianteId = null;

document.addEventListener('DOMContentLoaded', () => {
    console.log('=== INICIO: Carga de página ===');
    const urlParams = new URLSearchParams(window.location.search);
    estudianteId = urlParams.get('id');
    
    if (estudianteId) {
        console.log('Estudiante ID:', estudianteId);
        cargarInformacionEstudiante();
        cargarNotas();
    } else {
        alert('No se especificó un estudiante válido');
        window.location.href = 'index.html';
    }
});

async function cargarInformacionEstudiante() {
    try {
        const response = await fetch(`${API_URL}/estudiantes/${estudianteId}`);
        if (response.ok) {
            const estudiante = await response.json();
            document.getElementById('estudianteInfo').innerHTML = `
                <h2>Gestión de Notas para: ${estudiante.nombre} ${estudiante.apellido}</h2>
                <p><strong>Correo:</strong> ${estudiante.correo}</p>
            `;
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function cargarMaterias() {
    try {
        console.log('=== INICIANDO CARGA DE MATERIAS ===');
        const response = await fetch(`${API_URL}/materias`);
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const materias = await response.json();
        console.log('Materias recibidas del servidor:', materias);
        console.log('Número de materias:', materias.length);
        
        const select = document.getElementById('materiaSelect');
        if (!select) {
            console.error('ERROR: No existe elemento con id="materiaSelect"');
            return;
        }
        
        console.log('Limpiando dropdown...');
        select.innerHTML = '';
        
        console.log('Agregando opción por defecto...');
        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = 'Seleccione una materia...';
        select.appendChild(defaultOption);
        
        console.log('Agregando materias al dropdown...');
        materias.forEach((materia, index) => {
            console.log(`  [${index}] Agregando:`, materia);
            const option = document.createElement('option');
            option.value = materia.id;
            option.textContent = `${materia.nombre} (${materia.creditos} créditos)`;
            select.appendChild(option);
        });
        
        console.log('=== DROPDOWN POBLADO CON', materias.length, 'MATERIAS ===');
        console.log('Total de opciones en dropdown:', select.options.length);
        
    } catch (error) {
        console.error('ERROR AL CARGAR MATERIAS:', error);
        alert('Error al cargar las materias: ' + error.message);
    }
}

async function cargarNotas() {
    try {
        const response = await fetch(`${API_URL}/notas/estudiante/${estudianteId}`);
        const notas = await response.json();
        mostrarNotas(notas);
    } catch (error) {
        console.error('Error:', error);
    }
}

function mostrarNotas(notas) {
    const tbody = document.getElementById('notasList');
    tbody.innerHTML = '';
    
    if (notas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">No hay notas registradas</td></tr>';
        actualizarResumen([], 0, 0);
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
        row.insertCell(1).textContent = nota.materiaNombre || 'Sin nombre';
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
    
    actualizarResumen(notas, sumaPorcentajes, sumaContribuciones);
}

function actualizarResumen(notas, sumaPorcentajes, sumaContribuciones) {
    const resumenDiv = document.getElementById('resumen');
    const porcentajeRestante = 100 - sumaPorcentajes;
    
    resumenDiv.innerHTML = `
        <h3>Resumen de Calificaciones</h3>
        <p><strong>Suma de Porcentajes:</strong> ${sumaPorcentajes.toFixed(1)}%</p>
        <p><strong>Nota Acumulada:</strong> ${sumaContribuciones.toFixed(2)}</p>
        <p><strong>Porcentaje Restante:</strong> ${porcentajeRestante.toFixed(1)}%</p>
        ${sumaPorcentajes === 100 ? `<p><strong>Nota Final:</strong> ${sumaContribuciones.toFixed(2)} - ${sumaContribuciones >= 3 ? 'APROBADO ✅' : 'REPROBADO ❌'}</p>` : ''}
        ${sumaPorcentajes > 100 ? '<p style="color: red;"><strong>⚠️ Error:</strong> La suma de porcentajes supera el 100%</p>' : ''}
    `;
}

window.abrirFormularioNota = function() {
    console.log('=== ABRIENDO MODAL DE NOTA ===');
    const modal = document.getElementById('notaModal');
    if (!modal) {
        console.error('ERROR: No existe elemento con id="notaModal"');
        return;
    }
    
    modal.style.display = 'block';
    document.getElementById('modalTitulo').textContent = 'Agregar Nota';
    document.getElementById('notaForm').reset();
    document.getElementById('notaId').value = '';
    
    console.log('Modal abierto, cargando materias...');
    cargarMaterias();
};

window.cerrarModalNota = function() {
    const modal = document.getElementById('notaModal');
    if (modal) modal.style.display = 'none';
};

window.editarNota = function(nota) {
    const modal = document.getElementById('notaModal');
    if (modal) {
        modal.style.display = 'block';
        document.getElementById('modalTitulo').textContent = 'Editar Nota';
        document.getElementById('notaId').value = nota.id;
        cargarMaterias().then(() => {
            document.getElementById('materiaSelect').value = nota.materiaId;
        });
        document.getElementById('observacion').value = nota.observacion || '';
        document.getElementById('valor').value = nota.valor;
        document.getElementById('porcentaje').value = nota.porcentaje;
    }
};

window.guardarNota = async function() {
    const id = document.getElementById('notaId').value;
    const materiaId = parseInt(document.getElementById('materiaSelect').value);
    
    console.log('Guardando nota con materiaId:', materiaId);
    
    if (!materiaId || isNaN(materiaId)) {
        alert('Debe seleccionar una materia válida');
        return;
    }
    
    const nota = {
        materiaId: materiaId,
        observacion: document.getElementById('observacion').value,
        valor: parseFloat(document.getElementById('valor').value),
        porcentaje: parseFloat(document.getElementById('porcentaje').value),
        estudianteId: parseInt(estudianteId)
    };
    
    if (nota.valor < 0 || nota.valor > 5) {
        alert('La nota debe estar entre 0 y 5');
        return;
    }
    
    if (nota.porcentaje < 0 || nota.porcentaje > 100) {
        alert('El porcentaje debe estar entre 0 y 100');
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/notas${id ? '/' + id : ''}`, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nota)
        });
        
        if (response.ok) {
            alert(id ? 'Nota actualizada ✅' : 'Nota agregada ✅');
            cerrarModalNota();
            cargarNotas();
        } else {
            const error = await response.json();
            alert('Error: ' + (error.error || error.message || 'Ocurrió un error'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar la nota: ' + error.message);
    }
};

window.eliminarNota = async function(id) {
    if (confirm('¿Estás seguro de eliminar esta nota?')) {
        try {
            const response = await fetch(`${API_URL}/notas/${id}`, { method: 'DELETE' });
            if (response.ok) {
                alert('Nota eliminada ✅');
                cargarNotas();
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }
};

window.calcularNotaFinal = async function() {
    try {
        const response = await fetch(`${API_URL}/estudiantes/${estudianteId}/nota-final`);
        if (response.ok) {
            const data = await response.json();
            const notaFinal = data.notaFinal;
            
            document.getElementById('notaFinalResultado').innerHTML = `
                <div style="text-align: center; padding: 20px;">
                    <h3 style="color: ${notaFinal >= 3 ? '#4CAF50' : '#f44336'}; font-size: 24px;">
                        ${notaFinal >= 3 ? '✅' : '❌'} Nota Final: ${notaFinal}
                    </h3>
                    <p style="font-size: 18px; margin-top: 10px;">
                        <strong>${notaFinal >= 3 ? 'APROBADO' : 'REPROBADO'}</strong>
                    </p>
                    <button onclick="cerrarModalNotaFinal()" style="margin-top: 20px; padding: 10px 20px; background: #9C27B0; color: white; border: none; border-radius: 5px; cursor: pointer;">Cerrar</button>
                </div>
            `;
            document.getElementById('notaFinalModal').style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al calcular la nota final');
    }
};

window.cerrarModalNotaFinal = function() {
    const modal = document.getElementById('notaFinalModal');
    if (modal) modal.style.display = 'none';
};

window.volver = function() {
    window.location.href = 'index.html';
};
