package ar.edu.iw3.controllers;

import ar.edu.iw3.Constants;
import ar.edu.iw3.auth.model.business.interfaces.IUserAuthBusiness;
import ar.edu.iw3.model.business.interfaces.IUserBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.URL_USERS)
public class UserRestController extends BaseRestController {

    @Autowired
    private IUserBusiness userBusiness;

    // todo implement methods

}
