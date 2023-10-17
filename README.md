

```sequence
Client -> Fix client proxy: FIX:MsgType=NewOrderSingle
Fix client proxy -> Sequencer: CreateOrder
Sequencer -> Fix client proxy: OrderPending
Sequencer -> Order Manager: OrderPending 
Fix client proxy -> Client: FIX:MsgType=ExecutionReport
```