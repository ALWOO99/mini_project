function showDogBreeds() {
    console.log("Navigating to dog breeds page...");
    window.location.href = "/breeds"; // Ensure '/breeds' is a valid route in your app
}

// Function to fetch and display dog breeds dynamically
async function fetchDogBreeds() {
    try {
        const response = await fetch('https://api.thedogapi.com/v1/breeds');
        const data = await response.json();

        if (response.ok) {
            displayDogBreeds(data);
        } else {
            console.error("Failed to fetch breeds", data);
            alert("Failed to fetch dog breeds. Try again later.");
        }
    } catch (error) {
        console.error("Error fetching breeds:", error);
        alert("Error fetching dog breeds.");
    }
}

// Function to display breeds in a list
function displayDogBreeds(breeds) {
    const container = document.getElementById('dog-breeds-container') || document.body;
    container.innerHTML = ''; // Clear previous content

    breeds.forEach(breed => {
        const breedDiv = document.createElement('div');
        breedDiv.classList.add('breed-info');

        const nameElement = document.createElement('h2');
        nameElement.textContent = breed.name;

        const bredForElement = document.createElement('p');
        bredForElement.textContent = `Bred for: ${breed.bred_for || 'Not specified'}`;

        const breedGroupElement = document.createElement('p');
        breedGroupElement.textContent = `Breed Group: ${breed.breed_group || 'Not specified'}`;

        const lifeSpanElement = document.createElement('p');
        lifeSpanElement.textContent = `Life Span: ${breed.life_span}`;

        const temperamentElement = document.createElement('p');
        temperamentElement.textContent = `Temperament: ${breed.temperament || 'Not specified'}`;

        const originElement = document.createElement('p');
        originElement.textContent = `Origin: ${breed.origin || 'Not specified'}`;

        const imageElement = document.createElement('img');
        imageElement.setAttribute('data-breed', breed.name); // Use this to identify the breed for image loading
        imageElement.alt = breed.name;
        imageElement.style.width = '100px'; // Default size

        // Append elements to the breed div
        breedDiv.appendChild(nameElement);
        breedDiv.appendChild(imageElement); 
        breedDiv.appendChild(bredForElement);
        breedDiv.appendChild(breedGroupElement);
        breedDiv.appendChild(lifeSpanElement);
        breedDiv.appendChild(temperamentElement);
        breedDiv.appendChild(originElement);

        // Append breed info to the container
        container.appendChild(breedDiv);

        // Load image if it's visible
        if (breedDiv.offsetParent !== null) { // Check if the element is visible in the DOM
            loadDogImageForBreed(imageElement, breed.reference_image_id);
        }
    });
}

// Object to keep track of loaded images
let dogloadedImages = {};

// Modify the loadImageForBreed function
function loadDogImageForBreed(imageElement, imageId) {
    const breedName = imageElement.getAttribute('data-breed');
    if (dogloadedImages[breedName]) {
        // If image has been loaded, just set the src again
        imageElement.src = dogloadedImages[breedName];
        return;
    }

    if (imageId) {
        fetchDogImage(imageId)
            .then(imageUrl => {
                imageElement.src = imageUrl;
                // Store the URL for future reference
                dogloadedImages[breedName] = imageUrl;
            })
            .catch(error => {
                console.error(`Error fetching image for ${breedName}:`, error);
                imageElement.src = '/images/default-dog.jpg'; // Default image if fetching fails
                dogloadedImages[breedName] = '/images/default-dog.jpg'; // Store default image URL too
            });
    } else {
        imageElement.src = '/images/default-dog.jpg'; // Default image if no reference image
        dogloadedImages[breedName] = '/images/default-dog.jpg'; // Store default image URL
    }
}

// Function to handle breed search with scrolling
function searchBreed() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const breedListContainer = document.getElementById('dog-breeds-container');

    Array.from(breedListContainer.children).forEach(item => {
        const breedName = item.querySelector('h2') ? item.querySelector('h2').textContent.toLowerCase() : '';
        if (breedName.includes(searchInput)) {
            item.style.display = 'block';
            if (!document.querySelector('.highlighted')) { // Avoid scrolling if already highlighted
                scrollToBreed(searchInput);
            }
            // Load or reload image if it wasn't loaded yet or if it failed before
            const image = item.querySelector('img');
            if (!image.src || image.src.includes('default-dog.jpg')) {
                loadDogImageForBreed(image, image.getAttribute('data-breed'));
            }
        } else {
            item.style.display = 'none';
        }
    });
}

// Function to scroll to the breed
function scrollToBreed(breedName) {
    const breedElements = Array.from(document.querySelectorAll('.breed-info'));
    const targetBreed = breedElements.find(breed => {
        return breed.querySelector('h2') && breed.querySelector('h2').textContent.toLowerCase() === breedName.toLowerCase();
    });

    if (targetBreed) {
        targetBreed.scrollIntoView({ behavior: 'smooth', block: 'start' });
        targetBreed.classList.add('highlighted');
        setTimeout(() => targetBreed.classList.remove('highlighted'), 2000); // Remove highlight after 2 seconds
    } 
}

// Add an event listener to the search button
document.addEventListener('DOMContentLoaded', () => {
    fetchDogBreeds(); // Fetch breeds when the DOM is fully loaded

    const searchButton = document.getElementById('searchButton');
    if (searchButton) {
        searchButton.addEventListener('click', searchBreed);
    }

    // Attach event listener to search input for live filtering
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', searchBreed);
    }
});

// Function to fetch image URL
function fetchDogImage(imageId) {
    return fetch(`https://api.thedogapi.com/v1/images/${imageId}`)
        .then(response => response.json())
        .then(data => data.url)
        .catch(error => {
            console.error('Error fetching image:', error);
            throw error; // Re-throw to handle in the parent function
        });
}
