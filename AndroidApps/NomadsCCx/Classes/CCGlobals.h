/*
 * CCGlobals.h
 *
 *  Created on: Jul 23, 2012
 *      Author: TRock
 */

#ifndef CCGLOBALS_H_
#define CCGLOBALS_H_

#define kCursorSpriteZValue 100
#define kCursorSpriteTagValue 10
#define kCursorIdleTimer 3.0f

typedef enum {
    kStateIdle,
    kStateActive
} ObjectState;

typedef enum {
    kObjectTypeNone,
    kTypeCursor
} GameObjectType;


#endif /* CCGLOBALS_H_ */
