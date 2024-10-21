package frgp.utn.edu.ar.quepasa.controller.geo.SubnationalDivision;

import frgp.utn.edu.ar.quepasa.controller.geo.SubnationalDivisionController;
import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.geo.SubnationalDivisionUpdateRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.service.geo.SubnationalDivisionService;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class SubnationalDivisionControllerTest {

    @Mock
    private SubnationalDivisionService subnationalDivisionService;

    @InjectMocks
    private SubnationalDivisionController subnationalDivisionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSubnationalDivision() {
        SubnationalDivision division = new SubnationalDivision();
        division.setIso3("AR-B");
        division.setLabel("Buenos Aires");

        when(subnationalDivisionService.save(division)).thenReturn(division);

        ResponseEntity<SubnationalDivision> response = subnationalDivisionController.create(division);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(division, response.getBody());
        verify(subnationalDivisionService, times(1)).save(division);
    }

    @Test
    void testFindByIso() {
        String iso = "AR-B";
        SubnationalDivision division = new SubnationalDivision();
        division.setIso3(iso);
        division.setLabel("Buenos Aires");

        when(subnationalDivisionService.getById(iso)).thenReturn(division);

        ResponseEntity<SubnationalDivision> response = subnationalDivisionController.findByIso(iso);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(division, response.getBody());
        verify(subnationalDivisionService, times(1)).getById(iso);
    }

    @Test
    void testDeleteByIso() {
        String iso = "AR-B";

        doNothing().when(subnationalDivisionService).delete(iso);

        ResponseEntity<?> response = subnationalDivisionController.deleteByIso(iso);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(subnationalDivisionService, times(1)).delete(iso);
    }

    @Test
    void testUpdateSubnationalDivision() {
        String iso = "AR-B";
        SubnationalDivisionUpdateRequest request = new SubnationalDivisionUpdateRequest();
        request.setLabel("Buenos Aires Actualizado");

        SubnationalDivision updatedDivision = new SubnationalDivision();
        updatedDivision.setIso3(iso);
        updatedDivision.setLabel("Buenos Aires Actualizado");

        when(subnationalDivisionService.update(request, iso)).thenReturn(updatedDivision);

        ResponseEntity<SubnationalDivision> response = subnationalDivisionController.update(iso, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDivision, response.getBody());
        verify(subnationalDivisionService, times(1)).update(request, iso);
    }

    @Test
    void testHandleFail() {
        Fail failException = new Fail("Error de prueba");

        ResponseEntity<ResponseError> response = subnationalDivisionController.handleFail(failException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error de prueba", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationError() {
        ValidatorBuilder.ValidationError validationError = new ValidatorBuilder.ValidationError("Error de validación", null);

        ResponseEntity<ValidatorBuilder.ValidationError> response = subnationalDivisionController.handleValidationError(validationError);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(validationError, response.getBody());
    }
}

















