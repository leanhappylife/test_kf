# Deal Number Expansion – Project Plan and Estimation

## 1. Background
The project is to change **Deal Number** from `DECIMAL(5)` to `DECIMAL(25)`.

This change impacts not only database schema, but also stored procedures, downstream systems, reports, and cross-team testing.

### Current known scope
- 2000+ tables
- 300+ stored procedures
- 40 downstream systems
- HK / SG reports, around 800 in total
- Available core project resource: **3 people**

---

## 2. Project Objective
Ensure the Deal Number expansion is completed safely and without breaking:

- database objects
- stored procedures / batch jobs
- downstream system interfaces
- HK / SG reports
- end-to-end business processing

---

## 3. Key Delivery Principle
This is **not only a database change**.

The project includes:
- core database datatype update
- stored procedure / SQL update
- change support for 40 downstream systems
- integration testing with different teams
- regression testing for impacted reports
- business signoff and production validation

Because only **3 resources** are available, the overall timeline is mainly driven by:
- impact analysis workload
- cross-department communication
- downstream teams making their own changes
- integration testing and signoff

---

## 4. High-Level Project Plan

| Phase | Main Activity | Duration |
|---|---|---:|
| 1 | Impact analysis and solution design | 3 weeks |
| 2 | Core database / SP / SQL change | 5 weeks |
| 3 | Downstream discussion and change confirmation with 40 systems | 4 weeks |
| 4 | Downstream development support, integration testing, report regression, defect fixing | 6 weeks |
| 5 | UAT, production deployment and hypercare | 2 weeks |

**Total estimate: around 20 weeks**

---

## 5. Phase Details

### Phase 1 – Impact Analysis and Design
Main tasks:
- identify impacted tables and columns
- identify impacted stored procedures / SQL
- identify impacted downstream systems and reports
- confirm technical approach
- define testing scope and rollout approach

### Phase 2 – Core Database / SP / SQL Change
Main tasks:
- update schema definition
- update stored procedures
- update temp tables / session tables if needed
- update cast / concat / join logic
- prepare deployment scripts

### Phase 3 – Downstream Confirmation
Main tasks:
- communicate with 40 downstream teams
- confirm impact for each system
- confirm whether code / interface / report changes are needed
- align testing scope, timeline, and ownership

### Phase 4 – Integration Testing and Regression
Main tasks:
- support downstream teams during their changes
- execute SIT with impacted systems
- perform HK / SG report regression
- fix defects found during testing
- collect test evidence

### Phase 5 – UAT and Production Deployment
Main tasks:
- support user acceptance testing
- complete production deployment
- perform post-release validation
- provide hypercare support

---

## 6. Estimation Summary

### Timeline Estimate
- **Total project duration: around 20 weeks**

### Resource Assumption
- **3 core project resources only**

### Effort Estimate
Based on current scope and limited resource, the overall project effort is estimated at:

- **180 to 220 person-days**

This estimate includes:
- analysis
- development
- downstream coordination
- testing support
- defect fixing
- deployment and hypercare

---

## 7. Why the Timeline Is Long
The main effort is not only technical development.

The major time drivers are:
- 40 downstream systems also need to make changes
- coordination with different departments
- waiting for downstream confirmation and response
- arranging SIT / UAT windows
- large amount of report regression
- collecting final signoff

So the timeline is driven more by **cross-team dependency and testing** than by pure coding effort.

---

## 8. Main Risks
Key project risks:

- only 3 internal resources are available
- 40 downstream teams may have different priorities and schedules
- some downstream systems may need their own code changes
- report regression scope is large
- testing and signoff may take longer than expected

---

## 9. Recommendation
Recommended approach:

1. complete impact analysis first
2. start downstream communication early
3. prioritize critical systems and critical reports
4. execute testing in batches
5. reserve enough time for cross-team signoff

If earlier delivery is required, one of the following will be needed:
- additional project resources
- phased delivery by downstream group
- scope prioritization

---

## 10. Executive Summary for Management
**This is a cross-system and cross-department change project, not only a database datatype update. With only 3 available resources and 40 downstream systems also requiring changes, the realistic overall estimate is around 20 weeks. The main timeline driver is downstream coordination, system changes, integration testing, and signoff.**





















































# Deal Number Expansion – Testing Plan

## 1. Testing Objective
Ensure changing **Deal Number** from `DECIMAL(5)` to `DECIMAL(25)` will not impact:

- database processing
- stored procedures / batch jobs
- 40 downstream systems
- HK / SG reports
- end-to-end business flow

---

## 2. Testing Approach
Testing will be done in **5 layers**:

| Layer | Scope | Owner |
|---|---|---|
| Unit Test | DB / SP / SQL change validation | Core project team |
| SIT | Core system + downstream system integration | Core team + downstream teams |
| Report Regression | HK / SG impacted reports | Project team + report owners |
| UAT | Key business scenarios and signoff | Business users |
| Production Validation | Post-release validation | Core team + support teams |

---

## 3. What Will Be Tested

### Core Database / SP
- table datatype change
- stored procedure execution
- insert / update / merge / delete
- temp table / session table
- cast / concat / join logic
- boundary values / truncation / conversion errors

### Downstream Systems
- interface compatibility
- file / message format
- mapping logic
- application changes in each downstream system
- integration with core system

### Reports
- report SQL execution
- field display and format
- totals / counts / key data validation
- critical report regression

---

## 4. Testing Phases and Timeline

| Phase | Activity | Duration |
|---|---|---:|
| 1 | Unit testing | 2 weeks |
| 2 | SIT with downstream systems | 4 weeks |
| 3 | Report regression testing | 2 weeks |
| 4 | UAT and signoff | 2 weeks |
| 5 | Production validation / hypercare | 1 week |

**Total testing duration: around 11 weeks**

---

## 5. Key Testing Strategy
Because there are **40 downstream systems** and around **800 impacted reports**, testing will use a **priority-based approach**:

- **P1:** critical downstream systems / critical reports -> full testing
- **P2:** important systems / reports -> focused regression
- **P3:** lower-priority items -> smoke testing

This is necessary to keep testing manageable with only **3 resources**.

---

## 6. Entry and Exit Criteria

### Entry Criteria
- impact analysis completed
- impacted objects confirmed
- code changes completed
- test data prepared
- downstream teams informed

### Exit Criteria
- no critical defect open
- critical downstream systems passed SIT
- critical reports passed regression
- business UAT signoff received
- production rollback plan ready

---

## 7. Main Risk
The biggest challenge is **not only technical testing**, but also:

- coordination with **40 downstream teams**
- waiting for each team to complete their own changes
- arranging integration test windows
- collecting test evidence and signoff

So testing time includes both:

- execution time
- coordination time

---

## 8. Message to Management
**The testing effort is significant because this is a cross-system change.  
The main timeline driver is downstream coordination, integration testing, report regression, and business signoff, rather than only DB/SP technical change.**







    
