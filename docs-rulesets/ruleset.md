# Ruleset

A rulesets defines ...

- who can access an object in which role and object-state and
- which validation rules must be fullfilled for object mutations.

## involved tables

The ruleset configuration is hold in following tables:

- *egov_iam_rule_set*, main table with validation expression (field validation_entity) and ruleset priority
- *egov_iam_condition*, links action (= command) with ruleset and further defines if there is a state or role dependency to apply.
- *egov_iam_condition_allowed_for_role*, defines which condition is applied for which role
- *egov_iam_condition_allowed_for_state*, defines which condition is applied for which state

It is possible that there could be more than one ruleest activ in a certain situation (not the normal case).
To handle these situations, each ruleset has a priority: the ruleset with the highest priority will be applied.

If egov_iam_condition.allowed_for_all_roles is false then the roles defined in *egov_iam_condition_allowed_for_role* are differentiated by role.

If egov_iam_condition.allowed_for_all_state is false then the states defined in *egov_iam_condition_allowed_for_state* are differentiated by state.

Hint: set allowed_for_all_roles, allowed_for_all_state if you like to switch off the role dependency, e.g. if you have an user not yet configured with roles.

To ease the ruleset handling you can annotate DTOs as you see in this example:

```
package ch.ti8m.egov.demo.book.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Validation;
import lombok.Data;

import java.util.List;

@Ruleset(
    code = "UPDATE_BOOK",
    action = "BookApplicationService_UPDATE_BOOK",
    description = "Update Book",
    category = "Book"
)
@AllowedForRoles(roles = {"DEMO_ROLE"})
@AllowedForStates(states = {"NEW", "ACTIVE"})
@Data
@Validation(validation = "['NOT EQUALS', 'root', null]")
public class UpdateBookDto {

    @Validation(validation = "['MIN_MAX_LENGTH', 'root.title', '3', '50']")
    private String title;

    @Validation(validation = "['AND', ['GREATER_THAN', ['LENGTH', 'root.chapters'], 2], ['LESS_EQUAL_THAN', ['LENGTH', 'root.chapters'], 50]]")
    private List<CreateChapterDto> chapters;

    @Validation(validation = "['myCustomValidationMethod', 'root.language']")
    private String language;

}
```

# Round trip of ruleset configuration

if you a finished your annotation work, run maven plug-in 'ruleset'. This plug-in generates a file, named inserts.sql.
Execute the containing sql statements against your database.
Hint: Be carefull when executing the script, as all manually/directly added configuration in the tables will be overwritten.
Good message: if you only working with dto annotations, you can recreate and execute inserts.sql as often as you like.
