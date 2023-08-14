import copy
import time

# global variables
max_rule_length = 0
start_time = time.time()


def get_standardized_var_local():
    return "aa#"


def time_limit_reached():
    if (time.time() - start_time < 1100):
        return False
    return True


def read_input(file):
    lines = open(file).read().split('\n')
    query = (lines[0]).replace("~~", "")
    input_kb = lines[2:2 + int(lines[1])]
    kb_with_query = []
    kb_with_query.append(("~" + query).replace("~~", ""))
    for input in input_kb:
        input = input.replace("~~", "")
        if (input not in kb_with_query):
            kb_with_query.append(input)
    return kb_with_query


def write_output(contradiction_found):
    output = open('output.txt', 'w')
    output.write(contradiction_found)
    output.close()


def distribute_negation(rule):
    if ('~~' in rule):
        new_rule = rule.replace('~~', '')
    elif ('|' in rule):
        rule = rule.split('|', 1)
        new_rule = distribute_negation(
            rule[0].strip()) + ' & ' + distribute_negation(rule[1].strip())
    elif ('&' in rule):
        rule = rule.split('&', 1)
        new_rule = distribute_negation(
            rule[0].strip()) + ' | ' + distribute_negation(rule[1].strip())
    else:
        new_rule = ("~" + rule).replace("~~", "")
    return new_rule


def remove_implication(rule):
    if ('=>' in rule):
        rule = rule.split('=>', 1)
        return [distribute_negation(rule[0].strip()), rule[1].strip()]
    return [rule, None]


def distribute_and_over_or(rule, right):
    cnf_rules, rule = [], rule.replace('~~', '')
    if (right):
        cnf_rules = [rule.strip() + ' | ' + right for rule in rule.split('&')]
    else:
        new_rules = []
        for r in rule.split('|'):
            if ('&' in r):
                new_rules.append([i.strip() for i in r.strip().split('&')])
            else:
                new_rules.append([r.strip()])

        def generate_subset_combinations(new_rules, temp):
            if (len(temp) == len(new_rules)):
                cnf_rules.append(copy.deepcopy(temp))
                return
            for i in new_rules[len(temp)]:
                temp.append(i)
                generate_subset_combinations(new_rules, temp)
                temp.pop()

        generate_subset_combinations(new_rules, [])
        cnf_rules = [' | '.join(rule) for rule in cnf_rules]
    return cnf_rules


def remove_extra_spaces(rule):
    rule = rule.replace(' | ', '*|*')
    rule = rule.replace(' ', '')
    rule = rule.replace('*|*', ' | ')
    return rule


def cnf_conversion(original_kb):
    cnf_kb = []
    for rule in original_kb:
        split_rule = remove_implication(rule)
        cnf_rules = distribute_and_over_or(split_rule[0], split_rule[1])
        for rule in cnf_rules:
            rule = remove_extra_spaces(rule)
            rule, _ = standardize_variables_names(
                rule, get_standardized_var_local())

            cnf_kb.append(rule)
    return cnf_kb


def generate_predicate_map(cnf_kb, cnf_lookup_map, offset=0):
    global max_rule_length
    for rule_index, rule in enumerate(cnf_kb):

        predicates = rule.split(' | ')
        rule_length = len(predicates)
        max_rule_length = max(max_rule_length, rule_length)

        if (rule_length not in cnf_lookup_map):
            cnf_lookup_map[rule_length] = {}

        for predicate_index, predicate in enumerate(predicates):

            if ('(' not in predicate):
                predicate_names = predicate.strip()
                predicate_vars = []
            else:
                predicate_names = predicate.strip().split('(')[0]
                predicate_vars = predicate.strip().split(
                    '(')[1].split(')')[0].split(',')

            predicate_vars = [var.strip() for var in predicate_vars]

            if (predicate_names in cnf_lookup_map[rule_length]):
                cnf_lookup_map[rule_length][predicate_names].append({'vars': predicate_vars, 'rule': rule_index + offset,
                                                                     'index': predicate_index, 'length': rule_length})
            else:
                cnf_lookup_map[rule_length][predicate_names] = [{'vars': predicate_vars, 'rule': rule_index + offset,
                                                                 'index': predicate_index, 'length': rule_length}]
    return cnf_lookup_map


def check_rule_validity(rule):
    predicates = rule.split(' | ')
    for predicate in predicates:
        if (("~" + predicate).replace("~~", "") in predicates):
            return False
    return True


def remove_duplicate_predicates(rule):
    sorted_predicates = sorted(rule.split(' | '))
    unique_predicate_set = set(sorted_predicates)
    unique_predicates = []
    for r in sorted_predicates:
        if (r in unique_predicate_set):
            unique_predicates.append(r)
            unique_predicate_set.remove(r)
    return " | ".join(unique_predicates)


def substitute_vars(rule, old_var, new_var):
    rule = rule.replace(("(" + old_var + ","), ("(" + new_var + ","))
    rule = rule.replace(("," + old_var + ","), ("," + new_var + ","))
    rule = rule.replace(("," + old_var + ")"), ("," + new_var + ")"))
    rule = rule.replace(("(" + old_var + ")"), ("(" + new_var + ")"))
    return rule


def get_standardize_common_var(var):
    if (ord(var[1]) == ord('Z')):
        var = chr(ord(var[0])+1) + 'A#'
    else:
        var = var[0] + chr(ord(var[1])+1) + '#'
    return var


def get_standardize_local_var(var):
    if (ord(var[1]) == ord('z')):
        var = chr(ord(var[0])+1) + 'a%'
    else:
        var = var[0] + chr(ord(var[1])+1) + '%'
    return var


def standardize_variables_names(rule, next_var):

    local_var_map, new_rule = {}, rule
    for start in range(len(rule)):
        if (rule[start] == '('):
            for end in range(start + 1, len(rule), 1):
                if (rule[end] == ')'):
                    vars = rule[start + 1:end].split(',')
                    for j in range(len(vars)):
                        if (vars[j][0].islower() and vars[j] not in local_var_map):
                            next_var = get_standardize_local_var(next_var)
                            local_var_map[vars[j]] = next_var
                    break
    new_rule = rule
    for var in reversed(sorted(local_var_map.keys())):
        new_rule = substitute_vars(new_rule, var, local_var_map[var])
    new_rule = new_rule.replace('%', '#')
    return new_rule, next_var.replace('%', '#')


def update_cnf_kb_and_lookup_map(cnf_kb, inferred_cnf_base, cnf_lookup_map):
    total_kb_length = len(cnf_kb)
    new_cnf_rules = []
    for rule in inferred_cnf_base:
        if (rule not in cnf_kb):
            new_cnf_rules.append(rule)
            cnf_kb.append(rule)
    cnf_lookup_map = generate_predicate_map(
        new_cnf_rules, cnf_lookup_map, offset=total_kb_length)
    return cnf_kb, cnf_lookup_map


def is_unifiable(pred_var_1, pred_var_2):
    for i in range(len(pred_var_1)):
        if (pred_var_1[i] != pred_var_2[i] and pred_var_1[i][0].isupper() and pred_var_2[i][0].isupper()):
            return False
    return True


def rules_unification(rule_1, rule_2, rule_1_index, rule_2_index, rule_1_vars, rule_2_vars):

    if (not is_unifiable(rule_1_vars, rule_2_vars)):
        return "", False
    else:
        common_std_var = "aA#"
        for i, rule_1_var in enumerate(rule_1_vars):
            if (rule_1_var[0].isupper()):
                rule_2 = substitute_vars(rule_2, rule_2_vars[i], rule_1_var)
            elif (rule_2_vars[i][0].isupper()):
                rule_1 = substitute_vars(rule_1, rule_1_var, rule_2_vars[i])
            else:
                common_std_var = get_standardize_common_var(common_std_var)
                rule_1 = substitute_vars(rule_1, rule_1_var, common_std_var)
                rule_2 = substitute_vars(
                    rule_2, rule_2_vars[i], common_std_var)

    predicates_rule_1 = rule_1.split(' | ')
    predicates_rule_2 = rule_2.split(' | ')

    if (predicates_rule_1[rule_1_index][1:] != predicates_rule_2[rule_2_index]
            and predicates_rule_1[rule_1_index] != predicates_rule_2[rule_2_index][1:]):
        return "", False

    del predicates_rule_1[rule_1_index]
    del predicates_rule_2[rule_2_index]

    rule_1 = ' | '.join(predicates_rule_1)
    rule_2 = ' | '.join(predicates_rule_2)

    if (rule_2 == "" or rule_1 == ""):
        unified_rule = rule_1 + rule_2
    else:
        rule_1, next_std_var = standardize_variables_names(
            rule_1, 'aa#')
        rule_2, _ = standardize_variables_names(rule_2, next_std_var)
        unified_rule = rule_1 + " | " + rule_2

    unified_rule = remove_duplicate_predicates(unified_rule)
    unified_rule, _ = standardize_variables_names(
        unified_rule, get_standardized_var_local())
    if (not check_rule_validity(unified_rule)):
        return "", False

    return unified_rule, True


def unify_unit_rules_to_single_rule(cnf_kb, cnf_lookup_map, prev_found_rule):

    min_predicates_length = len(prev_found_rule.split(' | '))
    new_lookup = generate_predicate_map(
        [prev_found_rule], {}, offset=len(cnf_kb)-1)
    new_rule_map = new_lookup[min_predicates_length]
    unit_length_rule_map = cnf_lookup_map[1]
    contradiction_found = False

    for predicate in new_rule_map:

        not_predicate = ('~' + predicate).replace('~~', '')
        if (not_predicate not in unit_length_rule_map):
            continue

        unit_rules = unit_length_rule_map[not_predicate]
        for i in range(len(new_rule_map[predicate]) - 1, -1, -1):

            new_rule = new_rule_map[predicate][i]
            new_rule_cnf = cnf_kb[new_rule['rule']]
            new_rule_pos = new_rule['index']
            new_rule_vars = new_rule['vars']

            for unit_rule in unit_rules:

                unit_rule_cnf = cnf_kb[unit_rule['rule']]
                unit_rule_pos = unit_rule['index']
                unit_rule_vars = unit_rule['vars']
                unified_rule, is_valid = rules_unification(new_rule_cnf, unit_rule_cnf, new_rule_pos,
                                                           unit_rule_pos, new_rule_vars, unit_rule_vars)
                if (is_valid):

                    if (unified_rule == ""):
                        contradiction_found = True
                        return 0, contradiction_found, cnf_kb, cnf_lookup_map
                    if time_limit_reached():
                        return min_predicates_length, contradiction_found, cnf_kb, cnf_lookup_map
                    if (unified_rule in cnf_kb):
                        continue
                    cnf_kb, cnf_lookup_map = update_cnf_kb_and_lookup_map(
                        cnf_kb, [unified_rule], cnf_lookup_map)
                    new_pred_length, contradiction_found, cnf_kb, cnf_lookup_map = unify_unit_rules_to_single_rule(
                        cnf_kb, cnf_lookup_map, unified_rule)
                    min_predicates_length = min(
                        min_predicates_length, new_pred_length)
                    if (contradiction_found):
                        return 0, contradiction_found, cnf_kb, cnf_lookup_map

                if time_limit_reached():
                    return min_predicates_length, contradiction_found, cnf_kb, cnf_lookup_map

    return min_predicates_length, contradiction_found, cnf_kb, cnf_lookup_map


def unify_unit_rules_to_multiple_rules(cnf_kb, cnf_lookup_map, j_val):

    j_length_rules = cnf_lookup_map[j_val]
    unit_length_rules = cnf_lookup_map[1]
    contradiction_found, new_rule_found = False, False

    for predicate in j_length_rules:

        not_predicate = ('~' + predicate).replace('~~', '')
        if (not_predicate not in unit_length_rules):
            continue

        for i in range(len(j_length_rules[predicate]) - 1, -1, -1):

            j_rule = j_length_rules[predicate][i]
            j_rule_cnf = cnf_kb[j_rule['rule']]
            j_rule_pos = j_rule['index']
            j_rule_vars = j_rule['vars']

            for unit_rule in unit_length_rules[not_predicate]:

                unit_rule_cnf = cnf_kb[unit_rule['rule']]
                unit_rule_pos = unit_rule['index']
                unit_rule_vars = unit_rule['vars']
                unified_rule, is_valid = rules_unification(j_rule_cnf, unit_rule_cnf, j_rule_pos,
                                                           unit_rule_pos, j_rule_vars, unit_rule_vars)

                if (is_valid):
                    if (unified_rule == ""):
                        contradiction_found = True
                        return 0, new_rule_found, contradiction_found, unified_rule, cnf_kb, cnf_lookup_map
                    if (unified_rule in cnf_kb):
                        continue

                    cnf_kb, cnf_lookup_map = update_cnf_kb_and_lookup_map(
                        cnf_kb, [unified_rule], cnf_lookup_map)
                    new_rule_found = True
                    return len(unified_rule.split(' | ')), new_rule_found, contradiction_found, unified_rule, cnf_kb, cnf_lookup_map

                if time_limit_reached():
                    return j_val, new_rule_found, contradiction_found, "", cnf_kb, cnf_lookup_map

    return j_val + 1, new_rule_found, contradiction_found, "", cnf_kb, cnf_lookup_map


def unit_step_of_unit_preference(cnf_kb, cnf_lookup_map):

    j_val, contradiction_found = 1, False
    new_rule_found, prev_found_rule = False, ""
    while (j_val <= max_rule_length):

        if (j_val not in cnf_lookup_map):
            j_val += 1
            continue

        if (new_rule_found):
            new_j_val, contradiction_found, cnf_kb, cnf_lookup_map = unify_unit_rules_to_single_rule(
                cnf_kb, cnf_lookup_map, prev_found_rule)
            new_rule_found, prev_found_rule = False, ""
            if (contradiction_found):
                return contradiction_found, cnf_kb, cnf_lookup_map
            if (time_limit_reached()):
                return contradiction_found, cnf_kb, cnf_lookup_map
            if (new_j_val == 1):
                j_val = 1
            else:
                j_val += 1
        else:
            new_j_val, new_rule_found, contradiction_found, new_unified_rule, cnf_kb, cnf_lookup_map = unify_unit_rules_to_multiple_rules(
                cnf_kb, cnf_lookup_map, j_val)
            if (contradiction_found):
                return contradiction_found, cnf_kb, cnf_lookup_map
            if (time_limit_reached()):
                return contradiction_found, cnf_kb, cnf_lookup_map
            if (new_rule_found):
                prev_found_rule = new_unified_rule
            j_val = new_j_val

        if (time_limit_reached()):
            return contradiction_found, cnf_kb, cnf_lookup_map

    return contradiction_found, cnf_kb, cnf_lookup_map


def rule_factorization(rule, predicate_vars_1, predicate_vars_2, predicate_index_1, predicate_index_2):

    if (not is_unifiable(predicate_vars_1, predicate_vars_2)):
        return "", False
    else:
        common_std_var = "aA#"
        for i, predicate_var_1 in enumerate(predicate_vars_1):
            if (predicate_var_1[0].isupper()):
                rule = substitute_vars(
                    rule, predicate_vars_2[i], predicate_var_1)
            elif (predicate_vars_2[i][0].isupper()):
                rule = substitute_vars(
                    rule, predicate_var_1, predicate_vars_2[i])
            else:
                common_std_var = get_standardize_common_var(common_std_var)
                rule = substitute_vars(rule, predicate_var_1, common_std_var)
                rule = substitute_vars(
                    rule, predicate_vars_2[i], common_std_var)

    predicates = rule.split(' | ')
    if (predicates[predicate_index_1] != predicates[predicate_index_2]):
        return "", False
    del predicates[predicate_index_1]

    new_rule = ' | '.join(predicates)
    new_rule = remove_duplicate_predicates(new_rule)
    new_rule, _ = standardize_variables_names(
        new_rule, get_standardized_var_local())

    if (not check_rule_validity(new_rule)):
        return "", False
    return new_rule, True


def perform_factorization(cnf_kb, cnf_lookup_map, j_val):

    perform_unit_step, contradiction_found = False, False
    if (j_val not in cnf_lookup_map):
        return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map

    j_length_map = cnf_lookup_map[j_val]
    for predicate in j_length_map:

        predicate_length = len(j_length_map[predicate])
        for i in range(predicate_length - 1, -1, -1):

            rule_1 = j_length_map[predicate][i]
            rule_1_cnf = cnf_kb[rule_1['rule']]
            rule_1_pos = rule_1['index']
            rule_1_vars = rule_1['vars']

            for j in range(predicate_length - 1, -1, -1):
                if (i == j):
                    continue
                rule_2 = j_length_map[predicate][j]
                rule_2_pos = rule_2['index']
                rule_2_vars = rule_2['vars']

                if (rule_1['rule'] == rule_2['rule']):
                    factorized_rule, is_valid = rule_factorization(
                        rule_1_cnf, rule_1_vars, rule_2_vars, rule_1_pos, rule_2_pos)
                    if (is_valid):
                        if (factorized_rule in cnf_kb):
                            continue
                        if (time_limit_reached()):
                            return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map
                        cnf_kb, cnf_lookup_map = update_cnf_kb_and_lookup_map(
                            cnf_kb, [factorized_rule], cnf_lookup_map)
                        final_length, contradiction_found, cnf_kb, cnf_lookup_map = unify_unit_rules_to_single_rule(
                            cnf_kb, cnf_lookup_map, factorized_rule)

                        if (contradiction_found):
                            return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map
                        if (final_length == 1):
                            perform_unit_step = True
                            return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map
                    if (time_limit_reached()):
                        return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map

    return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map


def unify_multiple_rules_to_multiple_rules(cnf_kb, cnf_lookup_map, h_val, j_val):

    contradiction_found, perform_unit_step = False, False
    minimum_length = max(j_val, h_val)

    if (h_val not in cnf_lookup_map or j_val not in cnf_lookup_map):
        return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map

    j_length_map = copy.deepcopy(cnf_lookup_map[j_val])
    h_length_map = copy.deepcopy(cnf_lookup_map[h_val])
    for predicate in j_length_map:

        not_predicate = ('~' + predicate).replace('~~', '')
        if (not_predicate not in h_length_map):
            continue

        h_length_rules = h_length_map[not_predicate]
        for i in range(len(j_length_map[predicate]) - 1, -1, -1):

            j_rule = j_length_map[predicate][i]
            j_rule_cnf = cnf_kb[j_rule['rule']]
            j_rule_pos = j_rule['index']
            j_rule_vars = j_rule['vars']

            for h_rule in h_length_rules:

                h_rule_cnf = cnf_kb[h_rule['rule']]
                h_rule_pos = h_rule['index']
                h_rule_vars = h_rule['vars']

                unified_rule, is_valid = rules_unification(j_rule_cnf, h_rule_cnf, j_rule_pos,
                                                           h_rule_pos, j_rule_vars, h_rule_vars)

                if (is_valid):
                    if (unified_rule == ""):
                        contradiction_found = True
                        return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map
                    if (unified_rule in cnf_kb):
                        continue
                    if time_limit_reached():
                        return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map
                    cnf_kb, cnf_lookup_map = update_cnf_kb_and_lookup_map(
                        cnf_kb, [unified_rule], cnf_lookup_map)
                    predicates_length, contradiction_found, cnf_kb, cnf_lookup_map = unify_unit_rules_to_single_rule(
                        cnf_kb, cnf_lookup_map, unified_rule)
                    if (contradiction_found):
                        return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map
                    minimum_length = min(minimum_length, predicates_length)
                    if (minimum_length == 1):
                        perform_unit_step = True
                        return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map

                if time_limit_reached():
                    return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map

    return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map


def non_unit_step_of_unit_preference(cnf_kb, cnf_lookup_map):

    j_val, h_val = 2, 2
    while (h_val <= max_rule_length):

        perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map = perform_factorization(
            cnf_kb, cnf_lookup_map, j_val)
        if (contradiction_found or perform_unit_step):
            return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map
        if (j_val == 2):
            j_val += 1
        else:
            h_val, j_val = 2, j_val - 1
            while h_val <= j_val:

                perform_unit_step, contradiction_found, cnf_knowledge_base, cnf_lookup_map = unify_multiple_rules_to_multiple_rules(
                    cnf_kb, cnf_lookup_map, h_val, j_val)
                if (contradiction_found or perform_unit_step):
                    return perform_unit_step, contradiction_found, cnf_knowledge_base, cnf_lookup_map

                h_val += 1
                j_val -= 1

                if (time_limit_reached()):
                    return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map

            j_val = h_val + j_val

        if (time_limit_reached()):
            return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map

    return perform_unit_step, contradiction_found, cnf_kb, cnf_lookup_map


def infer_from_kb(cnf_kb, cnf_lookup_map):

    new_rule_found = True
    while (new_rule_found):

        new_rule_found = False
        contradiction_found, cnf_kb, cnf_lookup_map = unit_step_of_unit_preference(
            cnf_kb, cnf_lookup_map)
        if (contradiction_found):
            return True
        if (time_limit_reached()):
            return False

        perform_unit_step_again, contradiction_found, cnf_kb, cnf_lookup_map = non_unit_step_of_unit_preference(
            cnf_kb, cnf_lookup_map)
        if (contradiction_found):
            return True
        if (time_limit_reached()):
            return False
        if (perform_unit_step_again):
            new_rule_found = True
    return False


if (__name__ == "__main__"):
    try:
        write_output("FALSE")
        kb = read_input("input.txt")
        cnf_kb = cnf_conversion(kb)
        cnf_lookup_map = generate_predicate_map(cnf_kb, {})
        answer = str(infer_from_kb(cnf_kb, cnf_lookup_map)).upper()
        write_output(answer)
    except Exception as e:
        write_output("FALSE")
