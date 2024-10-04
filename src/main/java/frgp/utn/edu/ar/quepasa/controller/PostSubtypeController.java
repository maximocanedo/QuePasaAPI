package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post-subtypes")
public class PostSubtypeController {
    @Autowired
    private PostSubtypeService postSubtypeService;
    @Autowired
    private AuthenticationService authenticationService;

}
