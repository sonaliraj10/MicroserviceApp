package com.example.user.controller;

import com.example.user.client.DepartmentFeignClient;
import com.example.user.client.StudentFeignClient;
import com.example.user.entity.User;
import com.example.user.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Component
@RequestMapping("api/users")
public class UserController {

    @Autowired
    StudentFeignClient studentFeignClient;
    @Autowired
    DepartmentFeignClient departmentFeignClient;
    @Autowired
    private LoadBalancerClient loadBalancer;

    public static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static String UPLOADED_FOLDER = "C://Users//Shiwam singh//Downloads//";

    private UserService userService;

    @Value("${message}")
    private String message;

    @Autowired
    private ResourceLoader resourceLoader;

    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = userService.createUser(user);
        LOGGER.info("User detail saved successfully!");
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // build get user by id REST API
    // http://localhost:8080/api/users/1
    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long userId){
        User user = userService.getUserById(userId);
        LOGGER.info("User detail fetched successfully!");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Build Get All Users REST API
    // http://localhost:8080/api/users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        LOGGER.info("User details fetched successfully!");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Build Update User REST API
    @PutMapping("{id}")
    // http://localhost:8080/api/users/1
    public ResponseEntity<User> updateUser(@PathVariable("id") Long userId,
                                           @RequestBody User user){
        user.setId(userId);
        User updatedUser = userService.updateUser(user);
        LOGGER.info("User Updated Successfully!");
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Build Delete User REST API
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId){
        userService.deleteUser(userId);
        LOGGER.info("User Deleted Successfully!");
        return new ResponseEntity<>("User successfully deleted!", HttpStatus.OK);
    }


    @GetMapping("/msg")
    public ResponseEntity<String> getMsg(){
        LOGGER.info("Getting message :: " + message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/fileUpload")
    public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        Resource resourceFolder = resourceLoader.getResource("classpath:");
        Resource fileResource = resourceLoader.getResource("file:" + file.getOriginalFilename());
        FileCopyUtils.copy(file.getBytes(), resourceFolder.getFile().toPath().resolve(fileResource.getFilename()).toFile());

        /*File destinationFile = new File(resource.getURI().getPath()+file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationFile.toPath() , StandardCopyOption.REPLACE_EXISTING);*/
        return ResponseEntity.ok("File downloaded");
    }

    public InputStream readFile() {
        InputStream inputStream = null;
        try {
            //File file = ResourceUtils.getFile("classpath:");
            Resource resource = resourceLoader.getResource("users.json");
            inputStream = resource.getInputStream();
            //InputStream in = new FileInputStream(file);
            //properties.load(in);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return inputStream;
    }

    /*@PostMapping("/multi-upload")
    public ResponseEntity multiUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:");
        Arrays.asList(files)
                .stream()
                .forEach(file -> {
                    try {
                        Files.copy(file.getInputStream(), Paths.get(resource.getURI().getPath()+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return ResponseEntity.ok("File dounloaded");
    }

    public ResponseEntity uploadToLocalFileSystem(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path path = Paths.get(fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(fileName)
                .toUriString();
        LOGGER.info(fileDownloadUri);
        return ResponseEntity.ok(fileDownloadUri);
    }

//    public static File readFile(){
//        try {
//            File file = ResourceUtils.getFile("classpath:application.properties");
//            InputStream in = new FileInputStream(file);
//            properties.load(in);
//        } catch (IOException e) {
//            LOGGER.error(e.getMessage());
//        }
//        return properties;
//    }
testing
*/

    /** http://localhost:8081/StudentApp/getStudentName/3 **/
    @GetMapping("/getStudentName/{id}")
    @HystrixCommand(fallbackMethod = "converstionFailedFallback")
    public ResponseEntity<String> getStudentName(@PathVariable("id") String id){
        String name = studentFeignClient.getStudentNameById(id);
        LOGGER.info("Getting message :: " + name);
        return new ResponseEntity<>(name, HttpStatus.OK);
    }

    private String callStudentServiceAndGetData_Fallback(String id) {
        System.out.println("Student Service is down!!! fallback route enabled...");
        return "CIRCUIT BREAKER ENABLED!!! No Response From Student Service at this moment. " +
                " Service will be back shortly" ;
    }

    /** http://localhost:8082/DepartmentApp/getStudentMail/3 **/
    @GetMapping("/getStudentMailId/{id}")
    public ResponseEntity<String> getStudentMail(@PathVariable("id") String id){
        String mailById = departmentFeignClient.getStudentMailById(id);
        LOGGER.info("Getting message :: " + mailById);
        return new ResponseEntity<>(mailById, HttpStatus.OK);
    }

    public URI getStudentDetail() {
        ServiceInstance instance = loadBalancer.choose("StudentApplication");
        URI uri = instance.getUri();
        LOGGER.info("Getting uri :: " + uri);
        // Make a request to the service using the URI
        return uri;
    }



}
