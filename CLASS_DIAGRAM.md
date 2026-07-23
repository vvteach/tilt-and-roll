# Class diagram

```mermaid
classDiagram
    direction LR

    class GravityTiltGame {
        -Array~GameObject~ objects
        -Array~GameObject~ pendingDestroy
        -Array~CoinBurst~ coinBursts
        -Array~FireworkEffect~ fireworks
        -World world
        -Player player
        +buildWorld()
        +collect(Coin)
        +lose()
        +win()
        +triggerSpring(Spring)
        +queueJump()
    }

    class GameControls {
        -GravityTiltGame game
        +touchDown()
        +touchDragged()
        +touchUp()
    }

    class TextureAssets {
        +updateScale(Viewport)
        +drawPlatform()
        +drawHazard()
        +drawGoal()
        +drawSpring()
        +drawSplash()
        +drawHelp()
    }

    class LevelCatalog {
        -Array~LevelDefinition~ levels
        +get(int) LevelDefinition
        +size() int
    }

    class LevelDefinition {
        +getPlatforms()
        +getCoins()
        +getHazards()
        +getSprings()
        +getGoal()
    }

    class GameObject {
        <<abstract>>
        -Body body
        +getBody() Body
        +update(float)
        +render(ShapeRenderer)
        +render(SpriteBatch, TextureAssets)
        +destroy(World)
    }

    class BoxGameObject {
        <<abstract>>
        #width float
        #height float
    }

    class CircleGameObject {
        <<abstract>>
        #radius float
    }

    class Platform
    class Hazard
    class Goal
    class Spring {
        -boolean active
        +trigger()
        +isActive() boolean
    }
    class Coin
    class Player {
        +move(float)
        +jump(float)
        +setExpression(Expression)
    }
    class PlayerFoot {
        -Player player
        +getPlayer() Player
    }

    class BodyFactory {
        <<utility>>
        +createStaticBox()
        +createStaticCircleSensor()
        +createPlayer()
    }

    class GameContactListener {
        -GravityTiltGame game
        +beginContact(Contact)
        +endContact(Contact)
    }

    class CoinBurst
    class DizzyEffect
    class FireworkEffect

    GravityTiltGame --> GameControls : input
    GravityTiltGame --> TextureAssets : draws with
    GravityTiltGame --> LevelCatalog : loads
    LevelCatalog --> LevelDefinition : contains
    GravityTiltGame --> GameObject : keeps one array
    GravityTiltGame --> CoinBurst : creates
    GravityTiltGame --> DizzyEffect : owns
    GravityTiltGame --> FireworkEffect : creates
    GravityTiltGame --> GameContactListener : installs

    GameObject <|-- BoxGameObject
    GameObject <|-- CircleGameObject
    BoxGameObject <|-- Platform
    BoxGameObject <|-- Hazard
    BoxGameObject <|-- Goal
    BoxGameObject <|-- Spring
    CircleGameObject <|-- Coin
    CircleGameObject <|-- Player

    Player --> PlayerFoot : foot fixture userData
    Platform ..> BodyFactory : creates body
    Hazard ..> BodyFactory : creates body
    Goal ..> BodyFactory : creates body
