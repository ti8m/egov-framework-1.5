DELETE FROM egov_iam_condition_allowed_for_role;
DELETE FROM egov_iam_condition_allowed_for_state;
DELETE FROM egov_iam_condition;
DELETE FROM egov_iam_rule_set;

-- Class Name : ch.ti8m.egov.demo.domain.gescheaft.dto.CreateGescheaftDto
INSERT INTO egov_iam_rule_set (id, rule_set_code, description, category, rule_set_priority, validation_entity) VALUES (7110168500000000, 'CREATE_GESCHEAFT', 'Create Gescheaft', 'Gescheaft', 0, '{"fields":{"behandelndePersonen":{"fields":{},"type":"list","subtype":"int","validation":null},"uebernahmeDatum":{"fields":{},"type":"localdate","subtype":null,"validation":null},"geschaeftNummer":{"fields":{},"type":"string","subtype":null,"validation":null},"einreichungDatum":{"fields":{},"type":"localdate","subtype":null,"validation":null},"urheber":{"fields":{"vorname":{"fields":{},"type":"string","subtype":null,"validation":null},"name":{"fields":{},"type":"string","subtype":null,"validation":null},"id":{"fields":{},"type":"long","subtype":null,"validation":null}},"type":"object","subtype":null,"validation":null}},"type":"object","subtype":null,"validation":null}');
INSERT INTO egov_iam_condition (id, action, allowed_for_all_roles, allowed_for_all_states, rulesetid) VALUES (7110168500000001, 'GescheaftApplicationService_CREATE_GESCHEAFT', TRUE, TRUE, (SELECT id FROM egov_iam_rule_set WHERE rule_set_code = 'CREATE_GESCHEAFT'));