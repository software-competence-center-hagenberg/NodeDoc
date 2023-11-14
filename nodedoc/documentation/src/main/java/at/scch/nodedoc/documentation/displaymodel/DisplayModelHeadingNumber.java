package at.scch.nodedoc.documentation.displaymodel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DisplayModelHeadingNumber {

    private List<Integer> headingNumberComponents;

    public DisplayModelHeadingNumber(List<Integer> headingNumber) {
        this.headingNumberComponents = headingNumber;
    }

    public DisplayModelHeadingNumber() {
        this(List.of());
    }

    @Override
    public String toString() {
        return headingNumberComponents.stream().map(Object::toString).collect(Collectors.joining(".")) + ".";
    }
}
