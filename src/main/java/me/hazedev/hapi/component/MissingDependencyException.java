package me.hazedev.hapi.component;

public class MissingDependencyException extends RuntimeException {

    private final Class<? extends Component> dependency;

    public MissingDependencyException(Class<? extends Component> dependency) {
        this.dependency = dependency;
    }

    public Class<? extends Component> getDependency() {
        return dependency;
    }

    @Override
    public String getMessage() {
        return "Couldn't find dependency: " + dependency.getName();
    }
}
