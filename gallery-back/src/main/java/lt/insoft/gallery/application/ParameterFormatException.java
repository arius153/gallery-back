package lt.insoft.gallery.application;


public class ParameterFormatException extends RuntimeException{

    public ParameterFormatException(String column, String whatItIs, String whatItShouldBe)
    {
        super(column + " '" + whatItIs + "' should be in format: " + whatItShouldBe);
    }

}
