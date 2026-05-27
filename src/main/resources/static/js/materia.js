const API_URL = 'http://localhost:8081/api';

document.addEventListener('DOMContentLoaded', () => {
    cargarMaterias();
    document.getElementById('materiaForm').addEventListener('submit', guardarMateria);
});

async function cargarMaterias() {
    try {
        const response = await fetch(`${API_URL}/materias`);
        const materias = await response.json();
        mostrarMaterias(materias);
    } catch (error) {
        console.error('Error:', error);
    }
}

function mostrarMaterias(materias) {
    const tbody = document.getElementById('materiasList');
    tbody.innerHTML = '';
    
    materias.forEach(materia => {
        const row = tbody.insertRow();
        row.insertCell(0).textContent = materia.id;
        row.insertCell(1).textContent = materia.nombre;
        row.insertCell(2).textContent = materia.creditos;
        
        const actionsCell = row.insertCell(3);
        actionsCell.className = 'action-buttons';
        
        const editBtn = document.createElement('button');
        editBtn.textContent = 'Editar';
        editBtn.className = 'btn-edit';
        editBtn.onclick = () => editarMateria(materia);
        
        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Eliminar';
        deleteBtn.className = 'btn-delete';
        deleteBtn.onclick = () => eliminarMateria(materia.id);
        
        const verBtn = document.createElement('button');
        verBtn.textContent = 'Ver Estudiantes';
        verBtn.className = 'btn-ver';
        verBtn.onclick = () => alert('Función en desarrollo');
        
        actionsCell.appendChild(editBtn);
        actionsCell.appendChild(deleteBtn);
        actionsCell.appendChild(verBtn);
    });
}

window.abrirFormularioMateria = function() {
    document.getElementById('modalTitulo').textContent = 'Nueva Materia';
    document.getElementById('materiaForm').reset();
    document.getElementById('materiaId').value = '';
    document.getElementById('materiaModal').style.display = 'block';
};

window.cerrarModalMateria = function() {
    document.getElementById('materiaModal').style.display = 'none';
};

window.editarMateria = function(materia) {
    document.getElementById('modalTitulo').textContent = 'Editar Materia';
    document.getElementById('materiaId').value = materia.id;
    document.getElementById('nombre').value = materia.nombre;
    document.getElementById('creditos').value = materia.creditos;
    document.getElementById('materiaModal').style.display = 'block';
};

async function guardarMateria(e) {
    e.preventDefault();
    const id = document.getElementById('materiaId').value;
    const materia = {
        nombre: document.getElementById('nombre').value,
        creditos: parseInt(document.getElementById('creditos').value)
    };
    
    try {
        const url = id ? `${API_URL}/materias/${id}` : `${API_URL}/materias`;
        const method = id ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(materia)
        });
        
        if (response.ok) {
            alert(id ? 'Materia actualizada' : 'Materia creada');
            cerrarModalMateria();
            cargarMaterias();
        } else {
            const error = await response.json();
            alert('Error: ' + (error.error || 'Ocurrió un error'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar');
    }
}

window.eliminarMateria = async function(id) {
    if (confirm('¿Eliminar esta materia?')) {
        try {
            const response = await fetch(`${API_URL}/materias/${id}`, { method: 'DELETE' });
            if (response.ok) {
                alert('Materia eliminada');
                cargarMaterias();
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }
};
