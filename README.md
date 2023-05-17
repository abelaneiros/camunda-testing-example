# camunda-testing-example

TESTING PROBLEM (in **main** branch):
1. A principal process (CamundaExample2.bpmn) that calls 2 subprocesses.
2. Call Activity1 is mocked
3. Call Activity2 calls CamundaSubProcess2.bpmn. This process is deployed and executed as part of the test   

CamundaExample2.bpmn:
![CamundaExample2.bpmn](model-principal.png)

CamundaSubProcess2.bpmn
![CamundaSubProcess2.bpmn](model-subprocess2.png)
