import mongoose, {Schema, Document} from "mongoose";



interface IArticledata extends Document{
    data: any;
}


var thingSchema = new Schema<IArticledata>({
    data:{
        type: Schema.Types.Mixed,
        required: true
    }
},
{
    collection: "test"
}
);


const dataModel = mongoose.model<IArticledata>('articel', thingSchema, 'articles')


export {thingSchema, dataModel}