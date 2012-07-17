//  CommonProtocols.h

typedef enum {
    kStateIdle,
    kStateStretch
} ObjectStates;

typedef enum {
    kObjectTypeNone,
    kTypeCursor
} GameObjectType;

@protocol GameplayLayerDelegate
-(void)createObjectOfType:(GameObjectType)objectType 
               withHealth:(int)initialHealth
               atLocation:(CGPoint)spawnLocation 
               withZValue:(int)ZValue;
//-(void)createPhaserWithDirection:(PhaserDirection)phaserDirection
//                     andPosition:(CGPoint)spawnPosition;

@end
