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

import vttp.batchb.ssf.project.model.CatBreed;


@Service
public class CatBreedService {

    @Value("${thecatapi.key}")
    private String apiKey; 

    @Autowired
    private RestTemplate restTemplate;

    private static final String CAT_BREED_API_URL = "https://api.thecatapi.com/v1/breeds";

    public List<String> getCatBreeds() {
        // Make an API call to fetch dog breeds
        ResponseEntity<List<CatBreed>> response = restTemplate.exchange(
                CAT_BREED_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CatBreed>>() {});

        List<CatBreed> catBreeds = response.getBody();
        List<String> breedNames = new ArrayList<>();

        // Extract the breed names from the DogBreed objects
        for (CatBreed breed : catBreeds) {
            breedNames.add(breed.getName());  // Add the breed name to the list
        }

        return breedNames;  // Return the list of breed names (List<String>)
    }

    public CatBreed getBreedDetails(String breedName) {
        // Construct the URL to fetch details for the specific breed
        String url = CAT_BREED_API_URL + "/search?q=" + breedName;

        // Add the API key to the request header
        String apiUrlWithKey = url + "&api_key=" + apiKey;

        // Make a request to TheCatAPI to fetch breed details
        CatBreed[] breedDetailsArray = restTemplate.getForObject(apiUrlWithKey, CatBreed[].class);

        if (breedDetailsArray != null && breedDetailsArray.length > 0) {
            // Return the first breed match (or adjust as needed)
            return breedDetailsArray[0];
        } else {
            // Handle error case if no breed details are found
            return null;  // Or return a default breed object with placeholder data
        }
    }
}
