// Function to handle navigation to the cat breeds page
function showCatBreeds() {
    console.log("Navigating to cat breeds page...");
    window.location.href = "/cat-breeds"; // Updated route to '/cat-breeds'
}

// Function to fetch and display cat breeds dynamically
async function fetchCatBreeds() {
    try {
        const response = await fetch('https://api.thecatapi.com/v1/breeds');
        const data = await response.json();

        if (response.ok) {
            displayCatBreeds(data);
        } else {
            console.error("Failed to fetch breeds", data);
            alert("Failed to fetch cat breeds. Try again later.");
        }
    } catch (error) {
        console.error("Error fetching breeds:", error);
        alert("Error fetching cat breeds.");
    }
}

// Function to display cat breeds in a list
function displayCatBreeds(breeds) {
    const container = document.getElementById('cat-breeds-container') || document.body;
    container.innerHTML = ''; // Clear previous content

    breeds.forEach(breed => {
        const breedDiv = document.createElement('div');
        breedDiv.classList.add('breed-info');

        const nameElement = document.createElement('h2');
        nameElement.textContent = breed.name;

        const descriptionElement = document.createElement('p');
        descriptionElement.textContent = `Description: ${breed.description || 'Not specified'}`;

        const temperamentElement = document.createElement('p');
        temperamentElement.textContent = `Temperament: ${breed.temperament || 'Not specified'}`;

        const originElement = document.createElement('p');
        originElement.textContent = `Origin: ${breed.origin || 'Not specified'}`;

        const lifeSpanElement = document.createElement('p');
        lifeSpanElement.textContent = `Life Span: ${breed.life_span}`;

        const imageElement = document.createElement('img');
        imageElement.setAttribute('data-breed', breed.name);
        imageElement.alt = breed.name;
        imageElement.style.width = '100px';

        // Append elements to the breed div
        breedDiv.appendChild(nameElement);
        breedDiv.appendChild(imageElement);
        breedDiv.appendChild(descriptionElement);
        breedDiv.appendChild(temperamentElement);
        breedDiv.appendChild(originElement);
        breedDiv.appendChild(lifeSpanElement);

        container.appendChild(breedDiv);

        // Load image if visible
        if (breedDiv.offsetParent !== null) {
            loadCatImageForBreed(imageElement, breed.reference_image_id);
        }
    });
}

// Object to track loaded images
let catloadedImages = {};

// Function to load cat images
function loadCatImageForBreed(imageElement, imageId) {
    const breedName = imageElement.getAttribute('data-breed');
    if (catloadedImages[breedName]) {
        imageElement.src = catloadedImages[breedName];
        return;
    }

    if (imageId) {
        fetchCatImage(imageId)
            .then(imageUrl => {
                imageElement.src = imageUrl;
                catloadedImages[breedName] = imageUrl;
            })
            .catch(error => {
                console.error(`Error fetching image for ${breedName}:`, error);
                imageElement.src = 'path/to/default-image.jpg';
                catloadedImages[breedName] = 'path/to/default-image.jpg';
            });
    } else {
        imageElement.src = 'path/to/default-image.jpg';
        catloadedImages[breedName] = 'path/to/default-image.jpg';
    }
}

// Function to search and highlight cat breeds
function searchCatBreed() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const breedListContainer = document.getElementById('cat-breeds-container');

    Array.from(breedListContainer.children).forEach(item => {
        const breedName = item.querySelector('h2') ? item.querySelector('h2').textContent.toLowerCase() : '';
        if (breedName.includes(searchInput)) {
            item.style.display = 'block';
            if (!document.querySelector('.highlighted')) {
                scrollToBreed(searchInput, 'cat');
            }
        } else {
            item.style.display = 'none';
        }
    });
}

// Function to scroll to highlighted breed
function scrollToBreed(breedName, type) {
    const container = type === 'cat' ? 'cat-breeds-container' : 'dog-breeds-container';
    const breedElements = Array.from(document.getElementById(container).querySelectorAll('.breed-info'));
    const targetBreed = breedElements.find(breed => {
        return breed.querySelector('h2') && breed.querySelector('h2').textContent.toLowerCase() === breedName.toLowerCase();
    });

    if (targetBreed) {
        targetBreed.scrollIntoView({ behavior: 'smooth', block: 'start' });
        targetBreed.classList.add('highlighted');
        setTimeout(() => targetBreed.classList.remove('highlighted'), 2000);
    }
}

// Add event listeners
document.addEventListener('DOMContentLoaded', () => {
    fetchCatBreeds();

    const searchButton = document.getElementById('searchButton');
    if (searchButton) {
        searchButton.addEventListener('click', searchCatBreed);
    }

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', searchCatBreed);
    }
});

// Function to fetch cat image URL
function fetchCatImage(imageId) {
    return fetch(`https://api.thecatapi.com/v1/images/${imageId}`)
        .then(response => response.json())
        .then(data => data.url)
        .catch(error => {
            console.error('Error fetching image:', error);
            throw error;
        });
}
