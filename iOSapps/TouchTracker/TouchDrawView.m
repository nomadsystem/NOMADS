//
//  TouchDrawView.m
//  TouchTracker
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "TouchDrawView.h"
#import "Line.h"

@implementation TouchDrawView
@synthesize myLine;

-(id)initWithFrame:(CGRect)r
{
    self = [super initWithFrame:r];
    if (self) {
        linesInProcess = [[NSMutableDictionary alloc] init];
        
        completeLines = [[NSMutableArray alloc] init];
                
        [self setBackgroundColor:[UIColor whiteColor]];
        
        [self setMultipleTouchEnabled:YES];
    }
    return self;
}



-(void)drawRect:(CGRect)rect
{
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGRect bounds = [self bounds];
    
    CGPoint center;
    center.x = bounds.origin.x + bounds.size.width / 2.0;
    center.y = bounds.origin.y + bounds.size.height / 2.0;
    myFingerPoint = &center;
  //  [myLine setBegin:center];
  //  [myLine setEnd:center];
    
//    CGContextAddEllipseInRect(context,(CGRectMake (center.x, center.y, 12.0, 12.0)));
//                              CGContextDrawPath(context, kCGPathFill);
//                              CGContextStrokePath(context);
    
    [[UIColor redColor] set];
    CGContextSetLineWidth(context, 30.0);
    CGContextSetLineCap(context, kCGLineCapRound);
    CGContextMoveToPoint(context, [myLine end].x, [myLine end].y);
    CGContextAddLineToPoint(context, [myLine end].x, [myLine end].y);
    CGContextStrokePath(context);
    
    
//    //Draw complete lines in black
//    [[UIColor blackColor] set];
//    for (Line *line in completeLines) {
//        CGContextMoveToPoint(context, [line end].x, [line end].y);
//        CGContextAddLineToPoint(context, [line end].x, [line end].y);
//        CGContextStrokePath(context);
//    }
    
//    //Draw lines in process in red
//    [[UIColor redColor] set];
//    for (NSValue *v in linesInProcess) {
//        Line *line = [linesInProcess objectForKey:v];
//        CGContextMoveToPoint(context, [line end].x, [line end].y);
//        CGContextAddLineToPoint(context, [line end].x, [line end].y);
//        CGContextStrokePath(context);
//    }
}

-(void)clearAll
{
    //Clear the Collections
    [linesInProcess removeAllObjects];
    [completeLines removeAllObjects];
    
    //Redraw
    [self setNeedsDisplay];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    for (UITouch *t in touches) {
        NSLog(@" Touches Began");
        //Is this a double tap?
        if ([t tapCount] > 1) {
            [self clearAll];
            return;
        }
      //Use the touch object (packed in an NSValue) as the key
        NSValue *key = [NSValue valueWithNonretainedObject:t];
        
        //Create a line for the value
        CGPoint loc = [t locationInView:self];
        NSLog(@" loc = %f", loc.x);
        NSLog(@" loc = %f", loc.x);
        [myLine setBegin:loc];
        [myLine setEnd:loc];
        
        //Put pair in dictionary
//        [linesInProcess setObject:newLine forKey:key];
    }
}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    //Update linesInProcess with moved touches
    for (UITouch *t in touches) {
        NSValue *key = [NSValue valueWithNonretainedObject:t];
        
        //Find the line for this touch
     //   myLine = [linesInProcess objectForKey:key];
        
        //Update the line
        CGPoint loc = [t locationInView:self];
        [myLine setBegin:loc];
        [myLine setEnd:loc];
        NSLog(@" loc = %f", loc.x);
        NSLog(@" loc = %f", loc.x);
    }
    //Redraw
    [self setNeedsDisplay];
}

- (void)endTouches:(NSSet *)touches
{
    //Remove ending touches from dictionary
    for (UITouch *t in touches) {
        NSValue *key = [NSValue valueWithNonretainedObject:t];
        Line *line = [linesInProcess objectForKey:key];
        
        //If this is a double tap, 'line' will be nil,
        //so make sure not to add it to the array
        
        if (line) {
            [completeLines addObject:line];
            [linesInProcess removeObjectForKey:key];
        }
    }
    //Redraw
    [self setNeedsDisplay];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self endTouches:touches];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self endTouches:touches];
}

@end
