//
//  Cursor.m
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/16/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//

#import "Cursor.h"

@implementation Cursor

@synthesize swellAnim;
@synthesize shrinkAnim;
//@synthesize beingMoved;

#pragma mark -
-(void)changeState:(ObjectStates)newState {
    //NSLog(@"Cursor > changeState: %@", newState);
    [self stopAllActions];
    id action = nil;
    //    id movementAction = nil;
    //    CGPoint newPosition;
    [self setState:newState];
    switch (newState) {
//        case kStateIdle:
//            [self setDisplayFrame:[[CCSpriteFrameCache
//                                    sharedSpriteFrameCache]
//                                   spriteFrameByName:@"untitled_1.png"]];
//            break;
            
//        case kStateStretch:
//            [self setDisplayFrame:[[CCSpriteFrameCache
//                                    sharedSpriteFrameCache]
//                                   spriteFrameByName:@"untitled_12.png"]];
//            break;
        case kStateIdle:
            action = [CCAnimate actionWithAnimation:shrinkAnim];
             NSLog(@"Cursor > changeState() > set to kStateIdle");
            break;
        case kStateStretch:
            action = [CCAnimate actionWithAnimation:swellAnim];
            NSLog(@"Cursor > changeState() > set to kStateStretch");
            break;
    }
    if (action != nil) {
        //id repeatAction = [CCRepeatForever actionWithAction:action];
        NSLog(@"Cursor.m > action is !nil");
        [self runAction:action];
    }
}

#pragma mark -
-(void)updateStateWithDeltaTime:(ccTime)deltaTime
           andListOfGameObjects:(CCArray*)listOfGameObjects {
//    if (beingMoved){
//        NSLog(@"Cursor.m > setting to kStateStretch");
//        [self changeState:kStateStretch];
//    }
//    else {
//        NSLog(@"Cursor.m > setting to kStateIdle");
//        [self changeState:kStateIdle];
//    }
    /*
     if (self.characterState == kStateDead)
     return; // Nothing to do if the Viking is dead
     if ((self.characterState == kStateTakingDamage) && 
     ([self numberOfRunningActions] > 0))
     return; // Currently playing the taking damage animation
     
     // Check for collisions
     // Change this to keep the object count from querying it each time
     CGRect myBoundingBox = [self adjustedBoundingBox];
     for (GameCharacter *character in listOfGameObjects) {
     // This is Ole the Viking himself
     // No need to check collision with one's self
     if ([character tag] == kVikingSpriteTagValue)
     continue;
     CGRect characterBox = [character adjustedBoundingBox];
     if (CGRectIntersectsRect(myBoundingBox, characterBox)) {
     // Remove the PhaserBullet from the scene
     if ([character gameObjectType] == kEnemyTypePhaser) {
     [self changeState:kStateTakingDamage];
     [character changeState:kStateDead];
     } else if ([character gameObjectType] == 
     kPowerUpTypeMallet) {
     // Update the frame to indicate Viking is
     // carrying the mallet
     isCarryingMallet = YES;
     [self changeState:kStateIdle];
     // Remove the Mallet from the scene
     [character changeState:kStateDead];
     } else if ([character gameObjectType] == 
     kPowerUpTypeHealth) {
     [self setCharacterHealth:100.0f];
     // Remove the health power-up from the scene
     [character changeState:kStateDead];
     }
     }
     }
     
     [self checkAndClampSpritePosition];
     if ((self.characterState == kStateIdle) || 
     (self.characterState == kStateWalking) ||
     (self.characterState == kStateCrouching) ||
     (self.characterState == kStateStandingUp) || 
     (self.characterState == kStateBreathing)) {
     if (jumpButton.active) {
     [self changeState:kStateJumping];
     } else if (attackButton.active) {
     [self changeState:kStateAttacking];
     } else if ((joystick.velocity.x == 0.0f) && 
     (joystick.velocity.y == 0.0f)) {
     if (self.characterState == kStateCrouching)
     [self changeState:kStateStandingUp];
     } else if (joystick.velocity.y < -0.45f) {
     if (self.characterState != kStateCrouching)
     [self changeState:kStateCrouching];
     } else if (joystick.velocity.x != 0.0f) { // dpad moving
     if (self.characterState != kStateWalking)
     [self changeState:kStateWalking];
     [self applyJoystick:joystick
     forTimeDelta:deltaTime];
     } 
     }
     if ([self numberOfRunningActions] == 0) {
     // Not playing an animation
     if (self.characterHealth <= 0.0f) {
     [self changeState:kStateDead];
     } else if (self.characterState == kStateIdle) {
     millisecondsStayingIdle = millisecondsStayingIdle + 
     deltaTime;
     if (millisecondsStayingIdle > kVikingIdleTimer) {
     [self changeState:kStateBreathing];
     }
     } else if ((self.characterState != kStateCrouching) && 
     (self.characterState != kStateIdle)){
     millisecondsStayingIdle = 0.0f;
     [self changeState:kStateIdle];
     }
     }
     */
}
/*
 #pragma mark -
 -(CGRect)adjustedBoundingBox {
 // Adjust the bouding box to the size of the sprite 
 // without the transparent space
 
 CGRect vikingBoundingBox = [self boundingBox];
 float xOffset;
 float xCropAmount = vikingBoundingBox.size.width * 0.5482f;
 float yCropAmount = vikingBoundingBox.size.height * 0.095f;
 if ([self flipX] == NO) {
 // Viking is facing to the rigth, back is on the left
 xOffset = vikingBoundingBox.size.width * 0.1566f;
 } else {
 // Viking is facing to the left; back is facing right
 xOffset = vikingBoundingBox.size.width * 0.4217f;
 }
 vikingBoundingBox = 
 CGRectMake(vikingBoundingBox.origin.x + xOffset, 
 vikingBoundingBox.origin.y,
 vikingBoundingBox.size.width - xCropAmount, 
 vikingBoundingBox.size.height - yCropAmount);
 if (characterState == kStateCrouching) {
 // Shrink the bounding box to 56% of height
 // 88 pixels on top on iPad
 vikingBoundingBox = CGRectMake(vikingBoundingBox.origin.x,
 vikingBoundingBox.origin.y,
 vikingBoundingBox.size.width,
 vikingBoundingBox.size.height * 0.56f);
 }
 return vikingBoundingBox;
 }
 */

#pragma mark -
-(void)initAnimations {
    [self setSwellAnim:[self loadPlistForAnimationWithName:@"swellAnim"
                                              andClassName:NSStringFromClass([self class])]];
    [self setShrinkAnim:[self loadPlistForAnimationWithName:@"shrinkAnim"
                                               andClassName:NSStringFromClass([self class])]];
}

#pragma mark -
-(id) init {
    if( (self=[super init]) ) {
        self.gameObjectType = kTypeCursor;
        [self initAnimations];
//        [self changeState:kStateStretch];
    }
    return self;
}  

@end
