document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');

    function filterTable() {
        if (!searchInput) return;

        const searchTerm = searchInput.value.toLowerCase();
        const tableRows = document.querySelectorAll('table tbody tr');

        tableRows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    if (searchInput) {
        searchInput.addEventListener('keyup', filterTable);
    }

    if (searchButton) {
        searchButton.addEventListener('click', filterTable);
    }
});
