//
//  MovableObject.h
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/16/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//

#import "cocos2d.h"
#import "CCSprite.h"
#import "CommonProtocols.h"

@interface MovableObject : CCSprite {
    BOOL isActive;
    BOOL reactsToScreenBoundaries;
    CGSize screenSize;
    GameObjectType gameObjectType;
    ObjectStates state;
}

@property (readwrite) BOOL isActive;
@property (readwrite) CGSize screenSize;
@property (readwrite) GameObjectType gameObjectType;
@property (readwrite) BOOL reactsToScreenBoundaries;
@property (readwrite) ObjectStates state;

-(void)changeState:(ObjectStates)newState;
-(void)updateStateWithDeltaTime:(ccTime)deltaTime andListOfGameObjects:(CCArray*)listOfGameObjects; 
-(CGRect)adjustedBoundingBox;
-(void)checkAndClampSpritePosition; 
-(CCAnimation*)loadPlistForAnimationWithName:(NSString*)animationName andClassName:(NSString*)className;

@end
