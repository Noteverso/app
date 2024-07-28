export interface BaseNote {
  content: string;
  labels: { labelId: string; name: string }[];
  project: { projectId: string; name: string };
}

export interface FullNote extends BaseNote {
  noteId: string;
  timeStamp: string;
  fileNumber: number;
  linkedNoteNumber: number;
  linkingNoteNumber: number;
}

export type NewNote = BaseNote
