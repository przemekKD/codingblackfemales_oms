
# OMS Flow diagram

```plantuml
Client -> Fix client proxy: FIX:MsgType=NewOrderSingle
Fix client proxy -> Sequencer: CreateOrder
```