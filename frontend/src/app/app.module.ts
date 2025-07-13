import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CardModule} from "primeng/card";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from 'primeng/button';
import {HttpClientModule} from "@angular/common/http";
import {TableModule} from 'primeng/table';
import {NgxExtendedPdfViewerModule} from "ngx-extended-pdf-viewer";
import {PdfViewerComponent} from "./pdf-viewer/pdf-viewer.component";
import {DialogModule} from "primeng/dialog";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FileUploadModule} from "primeng/fileupload";
import {ToastModule} from "primeng/toast";
import {MessageService} from "primeng/api";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CardModule,
    FormsModule,
    InputTextModule,
    ButtonModule,
    HttpClientModule,
    TableModule,
    NgxExtendedPdfViewerModule,
    PdfViewerComponent,
    DialogModule,
    BrowserAnimationsModule,
    FileUploadModule,
    ToastModule
  ],
  providers: [MessageService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
