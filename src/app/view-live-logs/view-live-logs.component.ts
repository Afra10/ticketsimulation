import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-view-live-logs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-live-logs.component.html',
  styleUrls: ['./view-live-logs.component.css'],
})
export class ViewLiveLogsComponent implements OnInit, OnDestroy {
  latestLog: string = 'No logs received yet';
  private websocket: WebSocket | null = null;

  ngOnInit(): void {
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.disconnectWebSocket();
  }

  connectWebSocket(): void {
    this.websocket = new WebSocket('ws://localhost:8080/ws');

    this.websocket.onopen = () => {
      console.log('WebSocket connection established');
    };

    this.websocket.onmessage = (event) => {
      this.latestLog = event.data;
      console.log('New log message:', this.latestLog);
    };

    this.websocket.onerror = (error) => {
      console.error('WebSocket error:', error);
      this.latestLog = 'WebSocket error occurred';
    };

    this.websocket.onclose = () => {
      console.log('WebSocket connection closed');
    };
  }

  disconnectWebSocket(): void {
    if (this.websocket) {
      console.log('Disconnecting WebSocket');
      this.websocket.close();
      this.websocket = null;
    }
  }
}
