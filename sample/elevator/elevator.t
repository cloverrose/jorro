// Elevator
// height: 3
class Button(formula reset){
  var:
    req prop push;
    prop on;

  assume:
    // not push;
    G(F(not push));

  initial:
    // not on;

  service:
    G(push -> X(on U reset));
    G(reset -> not on);
    G((not on and not push) -> X(not on));
    // G((not on) -> (not on U push));
}

class Lift(const height){
  var:
    int where : 1..height;
    prop up;
    prop down;
    // prop open;

  macro:
    move = up or down;

  instance:
    goButtons[i in 1..height] : Button(reset=where==i);

  physical:
    G(up -> not down);
    G(down -> not up);

    // 動いているならドアは閉まっている
    // G(move -> not open);
    // ドアが開いていたら動かない
    // G(open -> not move);

    // 最上階・再下階
    G((where==1) -> not down);
    G((where==height) -> not up);

    // 止まっているなら次もその階
    AND(i in 1..height){
      G((where==i and not move) -> X(where==i))
    }

    // 上昇しているなら上の階に行く
    AND(i in 1..height-1){
      G((where==i and up) -> X(where==i+1))
    };

    // 下降しているなら下の階に行く
    AND(i in 2..height){
      G((where==i and down) -> X(where==i-1))
    };

  service:
    // 行き先ボタンが押されたらいつかはその階に行く
    AND(i in 1..height){
      G(goButtons[i].on -> F(where==i)
    };
}


class Elevator(const height){
  instance:
    lift : Lift(height=height);

    // フロアにある呼び出しボタン（上下区別なし）
    callButtons[i in 1..height] : Button(reset=lift.where==i);

  initial:
    lift.where==1;
    not lift.move;

  service:
    AND(i in 1..height){
      G(callButtons[i].on -> F(lift.where==i))
    };

  control:
    // 意味もなくドアを開かない
    // AND(i in 1..height){((lift.where==i) and not lift.goButtons[i].on and not callButtons[i].on) -> not lift.open};

    // 無駄に動かない
    G(AND(i in 1..height){not callButtons[i].on and not lift.goButtons[i].on}
      -> not lift.move);
}

system:
  Elevator(height=3);