package ru.sbercourse.filmlibrary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class FilmLibraryApp {

//    DirectorRepository directorRepository;

    public static void main(String[] args) {
        SpringApplication.run(FilmLibraryApp.class, args);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        System.out.println("Application is running........");
//        createDirectors();
//        directorRepository.findAll().forEach(d -> log.info("{}", d));
//        directorRepository.findById(3L).ifPresent(d -> log.info("{}", d));
//        directorRepository.findById(35L).ifPresent(d -> log.info("{}", d));
//        directorRepository.findAllById(Arrays.asList(1L, 4L)).forEach(d -> log.info("{}", d));
    }

//    private void createDirectors() {
//        for (int i = 1; i <= 10; i++) {
//            Director director = new Director();
//            director.setDirectorsFio("Director_" + i);
//            director.setPosition("pos_" + i);
//            directorRepository.save(director);
//        }
//    }
//
//    @Autowired
//    public void setDirectorRepository(DirectorRepository directorRepository) {
//        this.directorRepository = directorRepository;
//    }
}
