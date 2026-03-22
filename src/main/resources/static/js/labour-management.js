function openEditModal(id, username, role) {
    const editUserId = document.getElementById('editUserId');
    const editUsername = document.getElementById('editUsername');
    const editRole = document.getElementById('editRole');
    const editForm = document.getElementById('editEmployeeForm');
    const editModal = document.getElementById('editEmployeeModal');

    if (editUserId) editUserId.value = id;
    if (editUsername) editUsername.value = username;
    if (editRole) editRole.value = role;
    if (editForm) editForm.action = '/admin/labour/' + id + '/edit';
    if (editModal) editModal.classList.remove('hidden');
}

function closeEditModal() {
    const editModal = document.getElementById('editEmployeeModal');
    const editPassword = document.getElementById('editPassword');

    if (editModal) editModal.classList.add('hidden');
    if (editPassword) editPassword.value = ''; // Clear password field
}

// Attach to window so they are globally accessible for onclick handlers
window.openEditModal = openEditModal;
window.closeEditModal = closeEditModal;
