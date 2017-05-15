package de.compeople.xrm.customer;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    /**
     * Delivers the legacy (V1) version of the customer representation where the first
     * and last name are joined to one single property name:
     * <p>
     * <pre>
     * { "name": "Ralf Stuckert" }
     * </pre>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = "application/vnd.customer.v1+json")
    public Resource<Customer> getCustomerV1(@PathVariable("id") String id) {
        return getCustomerV2(id);
    }

    /**
     * Delivers the current (V2) version of the customer representation where
     * the name is split into different properties for first and last name
     * <p>
     * <pre>
     * { "firstName": "Ralf",
     *   "lastName": "Stuckert"
     * }
     * </pre>
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = "application/vnd.customer.v2+json")
    public Resource<Customer> getCustomerV2(@PathVariable("id") String id) {
        Customer customer = new Customer("Ralf", "Stuckert");
        return toResource(customer, id);
    }

    private Resource<Customer> toResource(Customer customer, String id) {
        Resource<Customer> customerResource = new Resource(customer);
        customerResource.add(getCustomerLink(id));
        return customerResource;
    }

    private Link getCustomerLink(String id) {
        return linkTo(methodOn(CustomerController.class).getCustomerV2(id)).withSelfRel();
    }

}
