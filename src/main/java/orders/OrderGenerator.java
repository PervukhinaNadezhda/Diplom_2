package orders;

import helper.GenerateRandom;
import user.User;

import java.util.List;

public class OrderGenerator {
    public static Order random(List<String> allIngredientsHashesList) {
        final List<String> orderIngredientsHashesList = List.of(
                allIngredientsHashesList.get(GenerateRandom.rndInt(allIngredientsHashesList.size())),
                allIngredientsHashesList.get(GenerateRandom.rndInt(allIngredientsHashesList.size())),
                allIngredientsHashesList.get(GenerateRandom.rndInt(allIngredientsHashesList.size())));
        return new Order(orderIngredientsHashesList);
    }
}
