## Assignment Announcement — System Testing (Messenger API)

## Deadline

* **Week 7 (January 09, Friday)**
    * Cannot be extended as we have one more assignment and two class tests afterward.

## Presentation
* **Week 8 (Tuesday - Section B and Wednesday - Section A)**
    * Each team will present their findings in a short presentation during class.
    * Focus on key defects found, risk areas, and recommendations.

## What you’ll do

You will perform **system testing** on the provided **Messenger API**. The API implements users, conversations (DIRECT/GROUP), messages, and block/unblock behavior with **JWT-based authentication** and **in-memory storage**.

* **Specifications & how to run:** Everything you need (endpoints, behaviors, and run instructions) is in **`documentation.md`**.
* **Do not** paste API specs in your report—**refer to `documentation.md`** instead.

## Important context

* This assignment is **supposed to be a system test**.
* There may be **intentional or accidental faults** in the implementation.
  **Finding, reproducing, and reporting those faults is your target.**
* You can use any tools or frameworks you like (Postman, REST-assured, custom scripts, etc.).

## Collaboration/Plagiarism policy

* You **may share “how to do something”** (setup, tools, how to structure a test plan) with other teams.
* You **must not share your test cases, test data, or full test ideas** with other teams.
* **Sharing test ideas** (beyond generic “how-to”) **may result in a penalty** for all involved teams.
* * Do **not copy** test ideas from external sources (LLMs, peers, internet).
* **All submitted work must be your team’s original work.**

## Deliverables (one PDF + supporting evidence)
Please, keep in mind there will be separate grading for the **report quality** and the **testing coverage/effectiveness**.
Include a single PDF report plus an evidence folder:

1. **Test Plan**

    * For each endpoint or behavior:

        * Write the input/parameter choices (use the class slides for guidance).
        * Describe the expected output and expected HTTP status code.

2. **Test Cases**

    * Present as a table/list with: **ID, Short Title, Pre-conditions, Steps, Expected, Actual, Status, Evidence link, Passed/Failed**.
    * Cover: happy paths, invalid inputs, auth checks, permission checks (self-update), membership checks, block/unblock effects, data consistency (IDs vs usernames), and edge cases.
    * Number of test cases is up to you; focus on quality and coverage.

3. **Defect Reports**

    * For each defect: **ID**, Title, Severity, **Steps to Reproduce**, Expected, Actual, Test Case No
    * Group similar defects; mark duplicates/cascades where relevant.

4. **Risk & Recommendations (≤1 page)**

    * Top risks you see in the current system
    * High-value fixes or hardening suggestions

5. **Individual Reflections (1 page for whole team)**
    * Each team member writes a short reflection on their contribution to the testing effort

### Evidence folder

* Include **screenshots**, **raw JSON responses**, and **logs** referenced in your report (organized and named by test/defect ID). 
* Postman or other tool exports are acceptable. In case of scripts, include relevant code snippets.

## Bonus (optional)

* If your team creates a **general-purpose testing tool** (e.g., reusable harness, scenario runner for JWT + conversation + messaging flows, fuzzing/property-based tester)—**beyond a basic fetch-and-match script**—you may earn bonus credit.

    * Provide the repository link in your report.

## Submission format

* Submit a single archive named as required in ELMS: **`<StudentID1>_..._<StudentIDn>_SystemTesting.zip`**

    * `<StudentID1>_..._<StudentIDn>_SystemTestingReport.pdf`
    * `evidence/` (screenshots, logs, JSON samples)

## Reminders

* No need to modify the code to “fix” issues—**report them**.
* Keep findings reproducible and tied to evidence.
* **All technical details about endpoints & running** are in **`documentation.md`**.
