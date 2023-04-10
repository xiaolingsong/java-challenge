package jp.co.axa.apidemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.controllers.EmployeeController;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USERNAME = "axal";
    private static final String PASSWORD = "Test1234";

    private static final String HOST_ROOT = "/api/v1/employees";

    @Test
    public void getAllEmployees() throws Exception{
        List<Employee> employees = new ArrayList<>(Arrays.asList(createEmployee(),createEmployee(),createEmployee(),createEmployee()));

        Mockito.when(employeeService.retrieveEmployees()).thenReturn(employees);

        mvc.perform(MockMvcRequestBuilders.get(HOST_ROOT)
                        .with(user(USERNAME).password(PASSWORD))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void getEmployeeByID() throws Exception{
        Employee employee = createEmployee();

        Mockito.when(employeeService.getEmployee(Mockito.anyLong())).thenReturn(employee);

        mvc.perform(MockMvcRequestBuilders.get(HOST_ROOT+"/0")
                .with(user(USERNAME).password(PASSWORD))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name",is(employee.getName())))
                .andExpect(jsonPath("salary",is(employee.getSalary())))
                .andExpect(jsonPath("department",is(employee.getDepartment())));
    }

    @Test
    public void deleteEmployeeByID() throws Exception{
        mvc.perform(MockMvcRequestBuilders.delete(HOST_ROOT+"/0")
                        .with(user(USERNAME).password(PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void addEmployee() throws Exception{
        Employee employee = createEmployee();
        employee.setId(1L);

        mvc.perform(MockMvcRequestBuilders.post(HOST_ROOT)
                .with(user(USERNAME).password(PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateEmployee() throws Exception{
        Employee employee = createEmployee();
        employee.setId(1L);
        Employee updateEmployee = new Employee();
        updateEmployee.setId(employee.getId());
        updateEmployee.setSalary(100);
        updateEmployee.setName("updated");
        updateEmployee.setDepartment("updated");

        Mockito.when(employeeService.getEmployee(employee.getId())).thenReturn(employee);

        mvc.perform(MockMvcRequestBuilders.put(HOST_ROOT + "/1")
                        .with(user(USERNAME).password(PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmployee)))
                .andExpect(status().isOk());
    }

    private Employee createEmployee(){
        Random random = new Random();
        int salary = random.nextInt(10);
        String[] departments = {"IT", "Marketing","HR","Operation"};

        Employee e = new Employee();
        e.setName("Test"+salary);
        e.setSalary(salary*10);
        e.setDepartment(departments[salary%4]);
        return e;

    }
}
