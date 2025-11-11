# Code Coverage Report

**Generated:** 2025-11-11  
**Tool:** JaCoCo 0.8.11  
**Test Framework:** JUnit 5 with Spring Boot Test

## Overall Coverage Summary

| Metric | Missed | Covered | Coverage % |
|--------|--------|---------|------------|
| **Instructions** | 291 | 1,348 | **78%** |
| **Branches** | 13 | 34 | **61%** |
| **Lines** | 70 | 264 | **79%** |
| **Methods** | 17 | 73 | **81%** |
| **Classes** | 0 | 10 | **100%** |

## Package-Level Coverage

### 1. Controllers Package (com.payan.demo.controller)
- **Instruction Coverage:** 70% (344/488 instructions)
- **Branch Coverage:** 85% (12/14 branches)
- **Line Coverage:** 74% (116/156 lines)
- **Method Coverage:** 88% (32/36 methods)
- **Classes Covered:** 4/4 (100%)

#### Breakdown by Class:
| Class | Instruction Coverage | Branch Coverage | Line Coverage | Method Coverage |
|-------|---------------------|-----------------|---------------|-----------------|
| TransactionController | 74% (159/214) | 100% (8/8) | 68% (34/50) | 100% (11/11) |
| UserController | 79% (132/166) | 100% (2/2) | 73% (27/37) | 100% (11/11) |
| DashboardController | 48% (42/87) | N/A (0/0) | 45% (10/22) | 43% (3/7) |
| LoginController | 52% (11/21) | 50% (2/4) | 71% (5/7) | 100% (3/3) |

### 2. Services Package (com.payan.demo.service)
- **Instruction Coverage:** 56% (181/323 instructions)
- **Branch Coverage:** 43% (7/16 branches)
- **Line Coverage:** 69% (63/91 lines)
- **Method Coverage:** 70% (28/40 methods)
- **Classes Covered:** 3/3 (100%)

#### Breakdown by Class:
| Class | Instruction Coverage | Branch Coverage | Line Coverage | Method Coverage |
|-------|---------------------|-----------------|---------------|-----------------|
| UserService | 100% (136/136) | 87% (7/8) | 100% (25/25) | 100% (11/11) |
| CustomUserDetailsService | 100% (38/38) | N/A (0/0) | 100% (8/8) | 100% (3/3) |
| TransactionService | 4% (7/149) | 0% (0/8) | 6% (2/30) | 14% (2/14) |

### 3. Configuration Package (com.payan.demo.config)
- **Instruction Coverage:** 100% (529/529 instructions)
- **Branch Coverage:** 50% (2/4 branches)
- **Line Coverage:** 100% (82/82 lines)
- **Method Coverage:** 100% (11/11 methods)
- **Classes Covered:** 2/2 (100%)

#### Breakdown by Class:
| Class | Instruction Coverage | Branch Coverage | Line Coverage | Method Coverage |
|-------|---------------------|-----------------|---------------|-----------------|
| SecurityConfig | 100% (130/130) | N/A (0/0) | 100% (25/25) | 100% (8/8) |
| DataLoader | 100% (399/399) | 50% (2/4) | 100% (57/57) | 100% (3/3) |

### 4. Main Application (com.payan.demo)
- **Instruction Coverage:** 37% (3/8 instructions)
- **Branch Coverage:** N/A (0/0 branches)
- **Line Coverage:** 33% (1/3 lines)
- **Method Coverage:** 50% (1/2 methods)
- **Classes Covered:** 1/1 (100%)

## Key Findings

### ✅ Strengths
1. **Excellent Controller Coverage:** Controllers have strong test coverage with 70% instruction and 85% branch coverage
2. **Complete Configuration Coverage:** Both SecurityConfig and DataLoader have 100% instruction coverage
3. **High Method Coverage:** 81% of all methods are covered by tests
4. **UserService & CustomUserDetailsService:** Both have 100% coverage

### ⚠️ Areas for Improvement
1. **TransactionService:** Only 4% instruction coverage - needs comprehensive testing
2. **DashboardController:** 48% instruction coverage - requires additional test cases
3. **LoginController:** 52% instruction coverage with 50% branch coverage
4. **Branch Coverage:** Overall branch coverage is 61% - conditional logic needs more test scenarios

## Test Statistics
- **Total Tests Run:** 72
- **Tests Passed:** 72
- **Tests Failed:** 0
- **Tests Skipped:** 0
- **Test Execution Time:** ~18.6 seconds

## Test Distribution
- Controller Tests: 38 tests
- Service Tests: 23 tests
- Repository Tests: 11 tests

## Coverage Report Locations
- **HTML Report:** `target/site/jacoco/index.html`
- **XML Report:** `target/site/jacoco/jacoco.xml`
- **CSV Report:** `target/site/jacoco/jacoco.csv`
- **Execution Data:** `target/jacoco.exec`

## Recommendations

### High Priority
1. **Add comprehensive tests for TransactionService** - Currently only 4% covered
2. **Improve DashboardController tests** - Add tests for admin vs user scenarios
3. **Enhance branch coverage** - Add more conditional logic tests

### Medium Priority
1. **Complete LoginController coverage** - Test error scenarios and edge cases
2. **Add integration tests** - For end-to-end user flows
3. **Test exception handling** - Verify error paths are properly tested

### Low Priority
1. **Review uncovered lines** - Check if they represent dead code or missing test cases
2. **Add performance tests** - Verify response times under load
3. **Security testing** - Add tests for authentication/authorization edge cases

## How to View the Report

### Open HTML Report
```bash
# Windows
start target/site/jacoco/index.html

# Mac/Linux
open target/site/jacoco/index.html
```

### Generate Report Again
```bash
mvn clean test
```

### Generate Report Without Running Tests
```bash
mvn jacoco:report
```

## Maven Configuration
The project now includes JaCoCo Maven plugin configured in `pom.xml`:
- Automatically generates reports after test execution
- Creates HTML, XML, and CSV reports
- Stores execution data for analysis

## Next Steps
1. Review the HTML report for detailed line-by-line coverage
2. Focus on improving TransactionService test coverage
3. Add missing branch coverage tests for conditional logic
4. Consider setting minimum coverage thresholds in the build process
