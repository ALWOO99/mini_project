package vttp.batchb.ssf.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import vttp.batchb.ssf.project.model.DogBreed;

@Service
public class DogBreedService {

    @Value("${thedogapi.key}")
    private String apiKey; 

    @Autowired
    private RestTemplate restTemplate;

    private static final String DOG_BREED_API_URL = "https://api.thedogapi.com/v1/breeds";

    public List<String> getDogBreeds() {
        // Make an API call to fetch dog breeds
        ResponseEntity<List<DogBreed>> response = restTemplate.exchange(
                DOG_BREED_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<DogBreed>>() {});

        List<DogBreed> dogBreeds = response.getBody();
        List<String> breedNames = new ArrayList<>();

        // Extract the breed names from the DogBreed objects
        for (DogBreed breed : dogBreeds) {
            breedNames.add(breed.getName());  // Add the breed name to the list
        }

        return breedNames;  // Return the list of breed names (List<String>)
    }

    public DogBreed getBreedDetails(String breedName) {
        // Construct the URL to fetch details for the specific breed
        String url = DOG_BREED_API_URL + "/search?q=" + breedName;

        // Add the API key to the request header
        String apiUrlWithKey = url + "&api_key=" + apiKey;

        // Make a request to TheDogAPI to fetch breed details
        DogBreed[] breedDetailsArray = restTemplate.getForObject(apiUrlWithKey, DogBreed[].class);

        if (breedDetailsArray != null && breedDetailsArray.length > 0) {
            // Return the first breed match (or adjust as needed)
            return breedDetailsArray[0];
        } else {
            // Handle error case if no breed details are found
            return null;  // Or return a default breed object with placeholder data
        }
    }
}

