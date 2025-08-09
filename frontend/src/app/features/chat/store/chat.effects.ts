import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { catchError, map, mergeMap, withLatestFrom } from 'rxjs/operators';
import { AiService } from 'src/app/core/services/ai.service';
import { ChatService } from '../services/chat.service';
import * as ChatActions from './chat.actions';
import { selectChatState } from './chat.selectors';
import { Message } from '../models/message.model';

@Injectable()
export class ChatEffects {
  // Load Messages Effect
  loadMessages$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.loadMessages),
      withLatestFrom(this.store.select(selectChatState)),
      mergeMap(([action, state]) =>
        this.chatService.getMessages(action.parentId, action.page).pipe(
          map((messages) =>
            ChatActions.loadMessagesSuccess({
              messages,
              hasMore: messages.length === 50
            })
          ),
          catchError((error) =>
            of(ChatActions.loadMessagesFailure({ error: error.message }))
          )
        )
      )
    )
  );

  // Search Messages Effect
  searchMessages$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.searchMessages),
      mergeMap((action) =>
        this.chatService.searchMessages(action.query, action.page).pipe(
          map((results) =>
            ChatActions.searchMessagesSuccess({ results })
          ),
          catchError((error) =>
            of(ChatActions.searchMessagesFailure({ error: error.message }))
          )
        )
      )
    )
  );

  // Load Message History Effect
  loadMessageHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.loadMessageHistory),
      mergeMap((action) =>
        this.chatService.getMessageHistory(action.messageId).pipe(
          map((history) =>
            ChatActions.loadMessageHistorySuccess({ history })
          ),
          catchError((error) =>
            of(ChatActions.loadMessageHistoryFailure({ error: error.message }))
          )
        )
      )
    )
  );

  // Send Message Effect
  sendMessage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.sendMessage),
      mergeMap((action) =>
        this.aiService.generateContent(action.content).pipe(
          map((response) => {
            const aiMessage: Message = {
              id: Date.now().toString(),
              content: response,
              role: 'assistant',
              timestamp: new Date()
            };
            return ChatActions.sendMessageSuccess({ message: aiMessage });
          }),
          catchError((error) =>
            of(ChatActions.sendMessageFailure({ error: error.message }))
          )
        )
      )
    )
  );

  // Load Thread Messages Effect
  loadThreadMessages$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.loadThreadMessages),
      mergeMap((action) =>
        this.chatService.getReplies(action.messageId).pipe(
          map((messages) =>
            ChatActions.loadThreadMessagesSuccess({ messages })
          ),
          catchError((error) =>
            of(ChatActions.loadThreadMessagesFailure({ error: error.message }))
          )
        )
      )
    )
  );

  // Typing Effects
  startTyping$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.startTyping),
      mergeMap(() => {
        this.chatService.startTyping();
        return of(ChatActions.updateTypingUsers({ users: ['currentUser'] }));
      })
    )
  );

  stopTyping$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChatActions.stopTyping),
      mergeMap(() => {
        this.chatService.stopTyping();
        return of(ChatActions.updateTypingUsers({ users: [] }));
      })
    )
  );

  constructor(
    private actions$: Actions,
    private store: Store,
    private chatService: ChatService,
    private aiService: AiService
  ) {}
}
