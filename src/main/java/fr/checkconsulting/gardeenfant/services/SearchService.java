package fr.checkconsulting.gardeenfant.services;

import fr.checkconsulting.gardeenfant.dto.FamilleDTO;
import fr.checkconsulting.gardeenfant.dto.NounouDto;
import fr.checkconsulting.gardeenfant.entity.Famille;
import fr.checkconsulting.gardeenfant.repository.FamilleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class SearchService {

    @Value("${nounou.application.url}")
    String nounouUrl;

    private final FamilleRepository familleRepository;
    private final RestTemplate restTemplate;

    public SearchService(FamilleRepository familleRepository, RestTemplate restTemplate) {
        this.familleRepository = familleRepository;
        this.restTemplate = restTemplate;
    }

    public List<NounouDto> getNounouByCriteria(String nom, String prenom, String ville, String debut, String fin, int jour) {
        LocalDateTime heureDebut = null;
        LocalDateTime heureFin = null;
        if (!Objects.equals(debut, "")) {
            heureDebut = LocalTime.parse(debut + ":00").atDate(LocalDate.now());
        }
        if (!Objects.equals(fin, "")) {
            heureFin = LocalTime.parse(debut + ":00").atDate(LocalDate.now());
        }

        String url = nounouUrl + "/api/v1/search/nounou?nom=" + nom + "&prenom=" + prenom + "&ville=" + ville + "&debut=" + heureDebut +   "&fin=" + heureFin   + "&jour=" + jour;
        ResponseEntity<NounouDto[]> nounouDtos = restTemplate.getForEntity(url, NounouDto[].class);

        return Arrays.stream(nounouDtos.getBody()).collect(Collectors.toList());

    }

    public List<FamilleDTO> getFamilleByCriteria(String nom, String prenom, String ville) {

        if ("".equals(nom)) nom = null;
        if ("".equals(prenom)) prenom = null;
        if ("".equals(ville)) ville = null;

        List<Famille> familles = familleRepository.getFamillesByCriteria(nom, prenom, ville);

        List<FamilleDTO> famillesDto = new ArrayList<>();
        familles.forEach(famille -> {

            FamilleDTO familleDTO = FamilleDTO.builder()
                    .nom(famille.getNom())
                    .prenom(famille.getPrenomRepresentant())
                    .adresse(String.join(" ", famille.getRue(), famille.getCodePostal(), famille.getVille()))
                    .mail(famille.getEmail())
                    .telephone(famille.getNumeroTelephone())
                    .build();

            famillesDto.add(familleDTO);
        });


        return famillesDto;
    }
}
