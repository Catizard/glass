package ptp.services;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String message) {
        return "Hello " + message;
    }
}
