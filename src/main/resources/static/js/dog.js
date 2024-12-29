function showDogBreeds() {
    console.log("Navigating to dog breeds page...");
    window.location.href = "/dog-breeds"; // Updated route to '/cat-breeds'
}

// Function to fetch and display cat breeds dynamically
async function fetchDogBreeds() {
    try {
        const response = await fetch('https://api.thedogapi.com/v1/breeds');
        const data = await response.json();

        if (response.ok) {
            displayDogBreeds(data);
        } else {
            console.error("Failed to fetch breeds", data);
            
        }
    } catch (error) {
        console.error("Error fetching breeds:", error);
       
    }
}

// Function to display cat breeds in a list
function displayDogBreeds(breeds) {
    const container = document.getElementById('dog-breeds-container') || document.body;
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
            loadDogImageForBreed(imageElement, breed.reference_image_id);
        }
    });
}

// Object to track loaded images
let dogloadedImages = {};

// Function to load cat images
function loadDogImageForBreed(imageElement, imageId) {
    const breedName = imageElement.getAttribute('data-breed');
    if (dogloadedImages[breedName]) {
        imageElement.src = dogloadedImages[breedName];
        return;
    }

    if (imageId) {
        fetchDogImage(imageId)
            .then(imageUrl => {
                imageElement.src = imageUrl;
                dogloadedImages[breedName] = imageUrl;
            })
            .catch(error => {
                console.error(`Error fetching image for ${breedName}:`, error);
                imageElement.src = 'path/to/default-image.jpg';
                dogloadedImages[breedName] = 'path/to/default-image.jpg';
            });
    } else {
        imageElement.src = 'path/to/default-image.jpg';
        dogloadedImages[breedName] = 'path/to/default-image.jpg';
    }
}

// Function to search and highlight cat breeds
function searchDogBreed() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const breedListContainer = document.getElementById('dog-breeds-container');

    Array.from(breedListContainer.children).forEach(item => {
        const breedName = item.querySelector('h2') ? item.querySelector('h2').textContent.toLowerCase() : '';
        if (breedName.includes(searchInput)) {
            item.style.display = 'block';
            if (!document.querySelector('.highlighted')) {
                scrollToBreed(searchInput, 'dog');
            }
        } else {
            item.style.display = 'none';
        }
    });
}

// Function to scroll to highlighted breed
function scrollToBreed(breedName, type) {
    const container = type === 'dog' ? 'dog-breeds-container' : 'cat-breeds-container';
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
    fetchDogBreeds();

    const searchButton = document.getElementById('searchButton');
    if (searchButton) {
        searchButton.addEventListener('click', searchDogBreed);
    }

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', searchDogBreed);
    }
});

// Function to fetch cat image URL
function fetchDogImage(imageId) {
    return fetch(`https://api.thedogapi.com/v1/images/${imageId}`)
        .then(response => response.json())
        .then(data => data.url)
        .catch(error => {
            console.error('Error fetching image:', error);
            throw error;
        });
}